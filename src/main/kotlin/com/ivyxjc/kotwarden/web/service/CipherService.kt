package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.Cipher
import com.ivyxjc.kotwarden.util.EMPTY_STRING
import com.ivyxjc.kotwarden.web.model.CipherRequestModel
import com.ivyxjc.kotwarden.web.model.KotwardenPrincipal
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.time.OffsetDateTime
import java.util.*

interface ICipherRepository {
    fun save(cipher: Cipher)
}

class CipherRepository(private val client: DynamoDbEnhancedClient) : ICipherRepository {
    private val schema = TableSchema.fromBean(Cipher::class.java)
    private val table = client.table(Cipher.TABLE_NAME, schema)

    override fun save(cipher: Cipher) {
        return table.putItem(cipher)
    }
}

class CipherService(private val cipherRepository: ICipherRepository, private val folderService: FolderService) {

    fun createCipher(kotwardenPrincipal: KotwardenPrincipal, request: CipherRequestModel) {
        val cipher = newCipher(request.type, request.name)
        cipher.userId = kotwardenPrincipal.id

        val folder = if (request.folderId?.isEmpty() == true) {
            folderService.findById(kotwardenPrincipal.id, kotwardenPrincipal.id) ?: error("Folder doesn't exist")
        } else {
            null
        }

        // TODO: 2022/6/27 handle attachments

        if (request.type == 1) {

        }

        cipher.name = request.name
        cipher.notes = request.notes
        cipher.passwordHistory = null
        cipher.reprompt = request.reprompt

        cipherRepository.save(cipher)
        //
//        moveToFolder(cipher.folderId, kotwardenPrincipal.id)
    }

    fun moveToFolder(folderId: String, userId: String) {

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