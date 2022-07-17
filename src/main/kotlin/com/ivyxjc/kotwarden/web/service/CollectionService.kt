package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.VaultCollection
import com.ivyxjc.kotwarden.util.COLLECTION_PREFIX
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema


interface IVaultCollectionRepository {
    fun save(vaultCollection: VaultCollection)

}

class VaultCollectionRepository(private val client: DynamoDbEnhancedClient) : IVaultCollectionRepository {
    private val schema = TableSchema.fromBean(VaultCollection::class.java)
    private val table = client.table(VaultCollection.TABLE_NAME, schema)

    override fun save(vaultCollection: VaultCollection) {
        vaultCollection.id = COLLECTION_PREFIX + vaultCollection.id
        table.putItem(vaultCollection)
    }
}