package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.Folder
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

interface IFolderRepository {
    fun findById(id: String, userId: String): Folder?
}


class FolderRepository(private val client: DynamoDbEnhancedClient) : IFolderRepository {
    private val schema = TableSchema.fromBean(Folder::class.java)
    private val table = client.table(Folder.TABLE_NAME, schema)

    override fun findById(id: String, userId: String): Folder? {
        val key = Key.builder().partitionValue(id).build()
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
}


class FolderService(private val folderRepository: IFolderRepository) {

    fun findById(id: String, userId: String): Folder? {
        return folderRepository.findById(id, userId)
    }
}