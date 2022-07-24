package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.Cipher
import com.ivyxjc.kotwarden.model.Folder
import com.ivyxjc.kotwarden.util.*
import com.ivyxjc.kotwarden.web.kError
import com.ivyxjc.kotwarden.web.model.*
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.*
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*

interface ICipherRepository {
    fun save(cipher: Cipher)

    fun delete(ownerId: String, id: String): Cipher?

    fun findByUser(userId: String): List<Cipher>

    fun findByOwnerAndId(ownerId: String, id: String): Cipher?

    fun findById(id: String): Cipher?

    fun listByOwnerId(ownerId: String): List<Cipher>

    fun updateCipherById(cipher: Cipher)

    fun updateFolderCiphersToNull(folderId: String)

    fun cleanCipherFolderId(cipher: Cipher)
}

class CipherRepository(private val client: DynamoDbEnhancedClient) : ICipherRepository {
    private val schema = TableSchema.fromBean(Cipher::class.java)
    private val table = client.table(Cipher.TABLE_NAME, schema)
    private val skIndex = table.index(Cipher.SK_INDEX)
    private val folderIndex = table.index(Cipher.FOLDER_SK_INDEX)

    override fun save(cipher: Cipher) {
        return table.putItem(cipher)
    }

    override fun delete(ownerId: String, id: String): Cipher? {
        return table.deleteItem(Key.builder().partitionValue(ownerId).sortValue(id).build())
    }

    override fun findByUser(userId: String): List<Cipher> {
        val queryConditional =
            QueryConditional.sortBeginsWith(Key.builder().partitionValue(userId).sortValue(CIPHER_PREFIX).build());
        val iter = table.query(queryConditional)
        return convert(iter)
    }

    override fun findByOwnerAndId(ownerId: String, id: String): Cipher? {
        val queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(ownerId).sortValue(id).build())
        val iter = table.query(queryConditional)
        val list = convert(iter)
        return if (list.isEmpty()) {
            null
        } else {
            list[0]
        }
    }

    override fun findById(id: String): Cipher? {
        val queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(id).build())
        val iter = skIndex.query(
            QueryEnhancedRequest.builder().queryConditional(queryConditional).build()
        )
        val list = convert(iter)
        return if (list.isNotEmpty()) {
            if (list.size > 1) {
                kError("duplicate cipher id")
            }
            return list[0]
        } else {
            null
        }
    }

    override fun listByOwnerId(ownerId: String): List<Cipher> {
        val queryConditional = QueryConditional.sortBeginsWith(
            Key.builder().partitionValue(ownerId)
                .sortValue(CIPHER_PREFIX).build()
        )
        val iter = table.query(queryConditional)
        return convert(iter)
    }

    override fun updateCipherById(cipher: Cipher) {
        val storedCipher = findById(cipher.id)!! ?: kError("Fail to share the cipher")
        val deleteRequest = TransactDeleteItemEnhancedRequest.builder()
            .key(Key.builder().partitionValue(storedCipher.ownerId).sortValue(cipher.id).build()).build()
        val insertRequest = TransactPutItemEnhancedRequest.builder(Cipher::class.java).item(cipher).build();
        val transaction = TransactWriteItemsEnhancedRequest.builder().addDeleteItem(table, deleteRequest)
            .addPutItem(table, insertRequest).build();
        client.transactWriteItems(transaction)
    }

    override fun updateFolderCiphersToNull(folderId: String) {
        val queryConditional = QueryConditional.sortBeginsWith(
            Key.builder().partitionValue(folderId)
                .sortValue(CIPHER_PREFIX).build()
        )
        val iter = folderIndex.query(queryConditional)
        val list = convert(iter)
        list.forEach {
            cleanCipherFolderId(it)
        }
    }

    override fun cleanCipherFolderId(cipher: Cipher) {
        if (cipher.folderId != null) {
            cipher.folderId = null
            save(cipher)
        }
    }
}


class CipherService(
    private val cipherRepository: ICipherRepository,
    private val folderService: FolderService,
    private val organizationService: OrganizationService,
    private val userOrganizationService: UserOrganizationService
) {

    fun createPlainCipher(principal: KotwardenPrincipal, request: CipherRequestModel): Cipher {
        val cipher = newCipher(request.type, request.name)
        cipher.ownerId = principal.id
        cipherRepository.save(cipher)
        return cipher
    }

    fun createCipher(kotwardenPrincipal: KotwardenPrincipal, request: CipherRequestModel): CipherResponseModel {
        val cipher = newCipher(request.type, request.name)
        return createUpdateCipherFromRequest(cipher, request, kotwardenPrincipal = kotwardenPrincipal)
    }

    fun createShareCipher(principal: KotwardenPrincipal, request: CipherCreateRequestModel): CipherResponseModel {
        val cipher = newCipher(request.cipher.type, request.cipher.name)
        return createUpdateCipherFromRequest(cipher, request.cipher, kotwardenPrincipal = principal)
    }

    fun shareCipherById(
        principal: KotwardenPrincipal, cipherId: String, request: CipherCreateRequestModel
    ): CipherResponseModel {
        val cipher = cipherRepository.findById(cipherId) ?: kError("Cipher does not exist")
        // TODO: 2022/7/22 check whether the user can access cipher
        if (isNotEmpty(request.cipher.organizationId)) {
            // TODO: 2022/7/22
        }
        return createUpdateCipherFromRequest(cipher, request.cipher, true, principal)
    }

    fun deleteCipher(kotwardenPrincipal: KotwardenPrincipal, id: String): CipherResponseModel {
        // TODO: 2022/7/18 delete by organization
        val cipher = cipherRepository.delete(kotwardenPrincipal.id, id) ?: kError("Cipher not found")
        return Cipher.converter.toResponse(cipher)
    }

    fun deleteCiphers(kotwardenPrincipal: KotwardenPrincipal, bulkDeleteRequestModel: CipherBulkDeleteRequestModel) {
        // TODO: 2022/7/18 delete by organization
        bulkDeleteRequestModel.ids.forEach {
            cipherRepository.delete(kotwardenPrincipal.id, it)
        }
    }

    fun updateCipher(
        kotwardenPrincipal: KotwardenPrincipal, cipherId: String, request: CipherRequestModel
    ): CipherResponseModel {
        val cipher = findById(kotwardenPrincipal.id, cipherId) ?: kError("Cipher doesn't exist")
        return createUpdateCipherFromRequest(cipher, request, kotwardenPrincipal = kotwardenPrincipal)
    }


    fun importCiphers(
        kotwardenPrincipal: KotwardenPrincipal, importData: ImportCiphersRequestModel
    ) {
        // TODO: 2022/7/18 how to rollback? if failed to import ciphers
        val folderList = mutableListOf<Folder>()
        importData.folders?.forEach {
            val folder = folderService.createFolder(kotwardenPrincipal, it)
            folderList.add(folder)
        }
        val map = mutableMapOf<Int, String>()
        importData.folderRelationships?.forEach {
            map[it.key!!] = folderList[it.value!!].id
        }
        importData.ciphers?.forEachIndexed { idx, it ->
            it.folderId = map[idx]
            createUpdateCipherFromRequest(newCipher(it.type, it.name), it, kotwardenPrincipal = kotwardenPrincipal)
        }

    }

    fun findByUser(userId: String): List<Cipher> {
        return cipherRepository.findByUser(userId)
    }

    fun findById(userId: String, cipherId: String): Cipher? {
        return cipherRepository.findByOwnerAndId(userId, cipherId)
    }

    fun listByOrganization(organizationId: String): List<Cipher> {
        return cipherRepository.listByOwnerId(organizationId)
    }

    private fun createUpdateCipherFromRequest(
        cipher: Cipher?,
        request: CipherRequestModel,
        forceUpdate: Boolean = false,
        kotwardenPrincipal: KotwardenPrincipal
    ): CipherResponseModel {
        cipher!!
        if (request.lastKnownRevisionDate != null && distance(
                cipher.updatedAt, request.lastKnownRevisionDate!!, ChronoUnit.MILLIS
            ) > 1
        ) {
            kError("The client copy of this cipher is out of date. Resync the client and try again.")
        }
        if (!isEmpty(cipher.organizationId) && cipher.organizationId != request.organizationId) {
            kError("Organization mismatch. Please re-sync the client before updating the cipher")
        }

        if (isNotEmpty(request.organizationId)) {
            val userOrganization =
                userOrganizationService.getByIdAndUser(request.organizationId!!, kotwardenPrincipal.id)
            if (userOrganization == null) {
                kError("You don't have permission to add item to organization")
            } else {
                // TODO: 2022/7/22 check where user have access to edit the cipher
                cipher.organizationId = request.organizationId
                cipher.ownerId = request.organizationId
            }
        } else {
            cipher.ownerId = kotwardenPrincipal.id
        }

        val folder = if (!isEmpty(request.folderId)) {
            folderService.findById(request.folderId!!, kotwardenPrincipal.id) ?: kError("Folder doesn't exist")
        } else {
            null
        }

        cipher.data = when (request.type) {
            1 -> encodeToString(request.login)!!
            2 -> encodeToString(request.secureNote)!!
            3 -> encodeToString(request.card)!!
            4 -> encodeToString(request.identity)!!
            else -> kError("Invalid type")
        }
        cipher.name = request.name
        cipher.notes = request.notes
        cipher.passwordHistory = null
        cipher.reprompt = request.reprompt
        cipher.folderId = folder?.id
        cipher.fields = encodeToString(request.fields)
        cipher.passwordHistory = encodeToString(request.passwordHistory)
        if (forceUpdate) {
            cipherRepository.updateCipherById(cipher)
        } else {
            cipherRepository.save(cipher)
        }

        val cipherResponseModel = Cipher.converter.toResponse(cipher, request)
        cipherResponseModel.edit = true
        cipherResponseModel.viewPassword = true
        cipherResponseModel.revisionDate = OffsetDateTime.now()
        return cipherResponseModel
    }

    private fun save(cipher: Cipher, forceUpdate: Boolean) {
        if (!forceUpdate) {
            cipherRepository.save(cipher)
        } else {
            val storedCipher = cipherRepository.findById(cipher.id)

        }
    }

    private fun newCipher(type: Int, name: String): Cipher {
        val cipher = Cipher()
        cipher.id = CIPHER_PREFIX + UUID.randomUUID().toString()
        cipher.createdAt = OffsetDateTime.now()
        cipher.updatedAt = OffsetDateTime.now()
        cipher.data = EMPTY_STRING
        cipher.type = type
        cipher.name = name
        return cipher
    }


}