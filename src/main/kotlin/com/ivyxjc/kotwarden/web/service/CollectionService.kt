package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.CollectionCipher
import com.ivyxjc.kotwarden.model.VaultCollection
import com.ivyxjc.kotwarden.util.COLLECTION_PREFIX
import com.ivyxjc.kotwarden.util.convert
import com.ivyxjc.kotwarden.web.kError
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest


interface IVaultCollectionRepository {
    fun save(vaultCollection: VaultCollection)
    fun listByOrganization(organizationId: String): List<VaultCollection>
    fun listByCollectionIds(collectionIds: List<String>): List<VaultCollection>
}


interface ICollectionCipherRepository {
    fun save(collectionCipher: CollectionCipher)

    fun listByCipherId(cipherId: String): List<CollectionCipher>

    fun listByCollectionId(collectionId: String): List<CollectionCipher>

    fun listByCollectionIds(collectionIds: List<String>): List<CollectionCipher>

}

class VaultCollectionRepository(private val client: DynamoDbEnhancedClient) : IVaultCollectionRepository {
    private val schema = TableSchema.fromBean(VaultCollection::class.java)
    private val table = client.table(VaultCollection.TABLE_NAME, schema)
    private val idx = table.index(VaultCollection.SK_INDEX)

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

    override fun listByCollectionIds(collectionIds: List<String>): List<VaultCollection> {
        val list = mutableListOf<VaultCollection>()

        collectionIds.forEach {
            // TODO: 2022/7/23 sort value should start with cipher- or organization-
            val queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(it).build())
            val resp = idx.query(
                QueryEnhancedRequest.builder().queryConditional(queryConditional).build()
            )
            val tmpList = convert(resp)
            if (tmpList.size > 1) {
                kError("Duplicate collection with same collection id")
            }
            if (tmpList.isEmpty()) {
                kError("Fail to find collection by id: $it")
            }
            list.add(tmpList[0])
        }
        return list
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

    override fun listByCollectionId(collectionId: String): List<CollectionCipher> {
        val queryConditional = QueryConditional
            .keyEqualTo(Key.builder().partitionValue(collectionId).build())
        val iter = table.query(queryConditional)
        return convert(iter)
    }

    override fun listByCollectionIds(collectionIds: List<String>): List<CollectionCipher> {
        val res = mutableListOf<CollectionCipher>()
        collectionIds.forEach {
            res.addAll(listByCollectionId(it))
        }
        return res
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
}