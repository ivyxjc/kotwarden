package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.Folder
import com.ivyxjc.kotwarden.util.FOLDER_PREFIX
import com.ivyxjc.kotwarden.web.model.FolderRequestModel
import com.ivyxjc.kotwarden.web.model.FolderResponseModel
import com.ivyxjc.kotwarden.web.model.KotwardenPrincipal
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.time.OffsetDateTime
import java.util.*


interface IFolderRepository {
    fun findById(id: String, userId: String): Folder?
    fun save(folder: Folder)
    fun updateFolder(id: String, userId: String, folder: Folder): Folder?
    fun deleteById(id: String, userId: String): Folder?
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

    override fun deleteById(id: String, userId: String): Folder? {
        val key = Key.builder().partitionValue(userId).sortValue(id).build()
        return table.deleteItem(key)
    }

    override fun updateFolder(id: String, userId: String, folder: Folder): Folder? {
        val key = Key.builder().partitionValue(userId).sortValue(id).build()
        val data: Folder = table.getItem { r -> r.key(key) } ?: return null
        data.name = folder.name
        return table.updateItem { r ->
            r.item(data)
        }
    }

    override fun save(folder: Folder) {
        folder.id = FOLDER_PREFIX + folder.id
        return table.putItem(folder)
    }
}

class FolderService(private val folderRepository: IFolderRepository) {
    fun createFolder(principal: KotwardenPrincipal, request: FolderRequestModel): FolderResponseModel {
        val folder = Folder()
        folder.name = request.name
        folder.id = UUID.randomUUID().toString()
        folder.userId = principal.id
        folder.createdAt = OffsetDateTime.now()
        folder.updatedAt = OffsetDateTime.now()
        folder.userId = principal.id
        folderRepository.save(folder)
        return Folder.converter.toFolderResponse(folder)
    }

    fun deleteFolder(principal: KotwardenPrincipal, id: String) {
        folderRepository.deleteById(id, principal.id) ?: error("Fail to find the folder")
    }

    fun updateFolder(principal: KotwardenPrincipal, id: String, request: FolderRequestModel): FolderResponseModel {
        val folder = Folder()
        folder.name = request.name
        val res = folderRepository.updateFolder(id, principal.id, folder) ?: error("Fail to find the folder")
        return Folder.converter.toFolderResponse(res)
    }

    fun findById(id: String, userId: String): Folder? {
        return folderRepository.findById(id, userId)
    }
}