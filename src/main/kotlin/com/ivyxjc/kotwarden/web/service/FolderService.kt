package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.Folder
import com.ivyxjc.kotwarden.web.model.FolderRequestModel
import com.ivyxjc.kotwarden.web.model.KotwardenPrincipal
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.time.OffsetDateTime
import java.util.*

interface IFolderRepository {
    fun findById(id: String, userId: String): Folder?
    fun save(folder: Folder)
}


class FolderRepository(private val client: DynamoDbEnhancedClient) : IFolderRepository {
    private val schema = TableSchema.fromBean(Folder::class.java)
    private val table = client.table(Folder.TABLE_NAME, schema)

    override fun findById(id: String, userId: String): Folder? {
        val key = Key.builder().partitionValue(userId).sortValue(id).build()
        val folder = table.getItem(key)
        if (folder == null) {
            return null
        } else {
            if (folder.userId != userId) {
                return null
            }
            return folder
        }
    }

    override fun save(folder: Folder) {
        return table.putItem(folder)
    }
}


class FolderService(private val folderRepository: IFolderRepository) {
    fun createFolder(principal: KotwardenPrincipal, request: FolderRequestModel) {
        val folder = Folder()
        folder.name = request.name
        folder.id = UUID.randomUUID().toString()
        folder.createdAt = OffsetDateTime.now()
        folder.updatedAt = OffsetDateTime.now()
        folder.userId = principal.id
        return folderRepository.save(folder)
    }

    fun findById(id: String, userId: String): Folder? {
        return folderRepository.findById(id, userId)
    }
}