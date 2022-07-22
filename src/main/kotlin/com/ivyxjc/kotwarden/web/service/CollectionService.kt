package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.VaultCollection
import com.ivyxjc.kotwarden.util.COLLECTION_PREFIX
import com.ivyxjc.kotwarden.util.convert
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest


interface IVaultCollectionRepository {
    fun save(vaultCollection: VaultCollection)

    fun listByOrganization(organizationId: String): List<VaultCollection>
}

class VaultCollectionRepository(private val client: DynamoDbEnhancedClient) : IVaultCollectionRepository {
    private val schema = TableSchema.fromBean(VaultCollection::class.java)
    private val table = client.table(VaultCollection.TABLE_NAME, schema)

    override fun save(vaultCollection: VaultCollection) {
        table.putItem(vaultCollection)
    }

    override fun listByOrganization(organizationId: String): List<VaultCollection> {
        val queryConditional =
            QueryConditional.sortBeginsWith(
                Key.builder().partitionValue(organizationId).sortValue(COLLECTION_PREFIX).build()
            );
        val iter = table.query(QueryEnhancedRequest.builder().queryConditional(queryConditional).build())
        return convert(iter)
    }

}