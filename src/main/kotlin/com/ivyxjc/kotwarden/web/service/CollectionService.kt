package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.CollectionCipher
import com.ivyxjc.kotwarden.model.UserCollection
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

interface ICollectionCipherRepository {
    fun save(collectionCipher: CollectionCipher)

    fun listByCipherId(cipherId: String): List<CollectionCipher>
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

class CollectionCipherRepository(private val client: DynamoDbEnhancedClient) : ICollectionCipherRepository {
    private val schema = TableSchema.fromBean(CollectionCipher::class.java)
    private val table = client.table(CollectionCipher.TABLE_NAME, schema)
    private val index = table.index(CollectionCipher.REVERSE_INDEX)

    override fun save(collectionCipher: CollectionCipher) {
        table.putItem(collectionCipher)
    }

    override fun listByCipherId(cipherId: String): List<CollectionCipher> {
        val queryConditional = QueryConditional
            .keyEqualTo(Key.builder().partitionValue(cipherId).build())
        val iter = index.query(queryConditional)
        return convert(iter)
    }
}

class CollectionService(
    private val collectionCipherRepository: ICollectionCipherRepository,
    private val userCollectionRepository: IUserCollectionRepository,
    private val collectionRepository: IVaultCollectionRepository
) {

    fun listCollectionIdsByCipher(cipherId: String): List<CollectionCipher> {
        return collectionCipherRepository.listByCipherId(cipherId)
    }

    fun listUserCollectionCollectionsByUser(userId: String): List<UserCollection> {
        return userCollectionRepository.listByUserId(userId)
    }
}