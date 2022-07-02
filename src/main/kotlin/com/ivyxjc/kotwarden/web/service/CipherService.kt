package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.Cipher
import com.ivyxjc.kotwarden.util.EMPTY_STRING
import com.ivyxjc.kotwarden.util.convert
import com.ivyxjc.kotwarden.web.model.CipherRequestModel
import com.ivyxjc.kotwarden.web.model.CipherResponseModel
import com.ivyxjc.kotwarden.web.model.KotwardenPrincipal
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import java.time.OffsetDateTime
import java.util.*

interface ICipherRepository {
    fun save(cipher: Cipher)

    fun findByUser(userId: String): List<Cipher>
}

class CipherRepository(private val client: DynamoDbEnhancedClient) : ICipherRepository {
    private val schema = TableSchema.fromBean(Cipher::class.java)
    private val table = client.table(Cipher.TABLE_NAME, schema)

    override fun save(cipher: Cipher) {
        return table.putItem(cipher)
    }

    override fun findByUser(userId: String): List<Cipher> {
        val queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build())
        val iter = table.query(queryConditional)
        return convert(iter)
    }


}

class CipherService(private val cipherRepository: ICipherRepository, private val folderService: FolderService) {

    fun createCipher(kotwardenPrincipal: KotwardenPrincipal, request: CipherRequestModel): CipherResponseModel {
        val cipher = newCipher(request.type, request.name)
        cipher.userId = kotwardenPrincipal.id

        val folder = if (request.folderId?.isEmpty() == true) {
            folderService.findById(kotwardenPrincipal.id, kotwardenPrincipal.id) ?: error("Folder doesn't exist")
        } else {
            null
        }

        cipher.data = when (request.type) {
            1 -> Json.encodeToString(request.login)
            2 -> Json.encodeToString(request.secureNote)
            3 -> Json.encodeToString(request.card)
            4 -> Json.encodeToString(request.identity)
            else -> error("Invalid type")
        }
        cipher.name = request.name
        cipher.notes = request.notes
        cipher.passwordHistory = null
        cipher.reprompt = request.reprompt
        cipher.fields = Json.encodeToString(request.fields)
        cipherRepository.save(cipher)

        val cipherResponseModel = Cipher.converter.toResponse(cipher, request)
        cipherResponseModel.edit = true
        cipherResponseModel.viewPassword = true
        cipherResponseModel.revisionDate = OffsetDateTime.now()
        return cipherResponseModel
    }

    fun findByUser(userId: String): List<Cipher> {
        return cipherRepository.findByUser(userId)
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