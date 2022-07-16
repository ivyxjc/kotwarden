package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.Cipher
import com.ivyxjc.kotwarden.util.*
import com.ivyxjc.kotwarden.web.kError
import com.ivyxjc.kotwarden.web.model.CipherRequestModel
import com.ivyxjc.kotwarden.web.model.CipherResponseModel
import com.ivyxjc.kotwarden.web.model.ImportCiphersRequestModel
import com.ivyxjc.kotwarden.web.model.KotwardenPrincipal
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import java.time.OffsetDateTime
import java.util.*

interface ICipherRepository {
    fun save(cipher: Cipher)

    fun findByUser(userId: String): List<Cipher>

    fun findByOwnerAndId(ownerId: String, id: String): Cipher?
}

class CipherRepository(private val client: DynamoDbEnhancedClient) : ICipherRepository {
    private val schema = TableSchema.fromBean(Cipher::class.java)
    private val table = client.table(Cipher.TABLE_NAME, schema)

    override fun save(cipher: Cipher) {
        cipher.id = CIPHER_PREFIX + cipher.id
        return table.putItem(cipher)
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
}


class CipherService(private val cipherRepository: ICipherRepository, private val folderService: FolderService) {

    fun createCipher(kotwardenPrincipal: KotwardenPrincipal, request: CipherRequestModel): CipherResponseModel {
        val cipher = newCipher(request.type, request.name)
        return createUpdateCipherFromRequest(cipher, request, kotwardenPrincipal)
    }

    fun deleteCipher(kotwardenPrincipal: KotwardenPrincipal, id: String): CipherResponseModel {
        TODO()
    }

    fun updateCipher(
        kotwardenPrincipal: KotwardenPrincipal, cipherId: String, request: CipherRequestModel
    ): CipherResponseModel {
        val cipher = findById(kotwardenPrincipal.id, cipherId) ?: kError("Cipher doesn't exist")
        return createUpdateCipherFromRequest(cipher, request, kotwardenPrincipal)
    }


    fun importCiphers(
        kotwardenPrincipal: KotwardenPrincipal, importData: ImportCiphersRequestModel
    ) {
        importData.ciphers?.forEach {
            createUpdateCipherFromRequest(newCipher(it.type, it.name), it, kotwardenPrincipal)
        }
    }

    fun findByUser(userId: String): List<Cipher> {
        return cipherRepository.findByUser(userId)
    }

    fun findById(userId: String, cipherId: String): Cipher? {
        return cipherRepository.findByOwnerAndId(userId, cipherId)
    }

    private fun deleteCipherById(userId: String, id: String) {
        TODO()
    }


    private fun createUpdateCipherFromRequest(
        cipher: Cipher?, request: CipherRequestModel, kotwardenPrincipal: KotwardenPrincipal
    ): CipherResponseModel {
        cipher!!
        if (!isEmpty(cipher.organizationId) && cipher.organizationId !== request.organizationId) {
            kError("Organization mismatch. Please re-sync the client before updating the cipher")
        }
        cipher.userId = kotwardenPrincipal.id

        val folder = if (!isEmpty(request.folderId)) {
            folderService.findById(kotwardenPrincipal.id, kotwardenPrincipal.id) ?: kError("Folder doesn't exist")
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
        cipherRepository.save(cipher)

        val cipherResponseModel = Cipher.converter.toResponse(cipher, request)
        cipherResponseModel.edit = true
        cipherResponseModel.viewPassword = true
        cipherResponseModel.revisionDate = OffsetDateTime.now()
        return cipherResponseModel
    }

    private fun newCipher(type: Int, name: String): Cipher {
        val cipher = Cipher()
        cipher.id = UUID.randomUUID().toString()
        cipher.createdAt = OffsetDateTime.now()
        cipher.updatedAt = OffsetDateTime.now()
        cipher.data = EMPTY_STRING
        cipher.type = type
        cipher.name = name
        return cipher
    }


}