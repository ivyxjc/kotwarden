package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.VaultCollection
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema


interface IVaultCollectionRepository {
    fun save(vaultCollection: VaultCollection)

}

class VaultCollectionRepository(private val client: DynamoDbEnhancedClient) : IVaultCollectionRepository {
    private val schema = TableSchema.fromBean(VaultCollection::class.java)
    private val table = client.table(VaultCollection.TABLE_NAME, schema)

    override fun save(vaultCollection: VaultCollection) {
        table.putItem(vaultCollection)
    }
}