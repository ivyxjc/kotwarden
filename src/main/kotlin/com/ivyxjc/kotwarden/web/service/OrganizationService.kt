package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.Organization
import com.ivyxjc.kotwarden.model.User
import com.ivyxjc.kotwarden.model.UserOrganization
import com.ivyxjc.kotwarden.model.VaultCollection
import com.ivyxjc.kotwarden.util.COLLECTION_PREFIX
import com.ivyxjc.kotwarden.util.ORGANIZATION_PREFIX
import com.ivyxjc.kotwarden.util.combine
import com.ivyxjc.kotwarden.util.convert
import com.ivyxjc.kotwarden.web.kError
import com.ivyxjc.kotwarden.web.model.CipherCollectionsRequestModel
import com.ivyxjc.kotwarden.web.model.CollectionRequestModel
import com.ivyxjc.kotwarden.web.model.KotwardenPrincipal
import com.ivyxjc.kotwarden.web.model.OrganizationCreateRequestModel
import org.slf4j.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.OffsetDateTime
import java.util.*

interface IUserOrganizationRepository {
    fun save(userOrganization: UserOrganization)
    fun listConfirmedByUserId(userId: String): List<UserOrganization>
    fun getByOrganizationAndUser(organizationId: String, userId: String): UserOrganization?
    fun listByOrganization(organizationId: String): List<UserOrganization>
}

interface IOrganizationRepository {
    fun save(organization: Organization)

    fun listByOrganizationIds(organizationIds: List<String>): List<Organization>
}

class UserOrganizationRepository(private val client: DynamoDbEnhancedClient) : IUserOrganizationRepository {
    companion object {
        private val log = LoggerFactory.getLogger(UserOrganizationRepository::class.java)
    }

    private val schema = TableSchema.fromBean(UserOrganization::class.java)
    private val table = client.table(UserOrganization.TABLE_NAME, schema)
    private val idx = table.index(UserOrganization.REVERSE_INDEX)

    override fun save(userOrganization: UserOrganization) {
        table.putItem(userOrganization)
    }

    override fun listConfirmedByUserId(userId: String): List<UserOrganization> {
        val queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build())
        val filter = Expression.builder().expression("#status = :status").expressionNames(mapOf("#status" to "Status"))
            .expressionValues(mapOf(":status" to AttributeValue.fromN(UserOrganization.Status.Confirmed.toString())))
            .build()
        val iter = table.query(
            QueryEnhancedRequest.builder().queryConditional(queryConditional).filterExpression(filter).build()
        )
        return convert(iter)
    }

    override fun getByOrganizationAndUser(organizationId: String, userId: String): UserOrganization? {
        val queryConditional =
            QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).sortValue(organizationId).build())
        val filter = Expression.builder().expression("#status = :status")
            .expressionNames(mapOf("#status" to "Status"))
            .expressionValues(mapOf(":status" to AttributeValue.fromN(UserOrganization.Status.Confirmed.toString())))
            .build()
        val iter = table.query(
            QueryEnhancedRequest.builder().queryConditional(queryConditional).filterExpression(filter).build()
        )
        val list = convert(iter)
        if (list.isEmpty()) {
            return null
        }
        if (list.size > 1) {
            log.error("[Duplicate][UserOrganization] duplicate records with same id+userId")
            kError("Internal data error, please contact system administrator")
        }
        return list[0]
    }

    override fun listByOrganization(organizationId: String): List<UserOrganization> {
        val queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(organizationId).build())
        return convert(idx.query(queryConditional))
    }
}

class OrganizationRepository(private val client: DynamoDbEnhancedClient) : IOrganizationRepository {

    private val schema = TableSchema.fromBean(Organization::class.java)
    private val table = client.table(Organization.TABLE_NAME, schema)


    override fun save(organization: Organization) {
        table.putItem(organization)
    }

    override fun listByOrganizationIds(organizationIds: List<String>): List<Organization> {
        val batches = ReadBatch.builder(Organization::class.java).mappedTableResource(table)
        organizationIds.forEach { it -> batches.addGetItem(Key.builder().partitionValue(it).sortValue(it).build()) }
        val request = BatchGetItemEnhancedRequest.builder().readBatches(batches.build()).build()
        return convert(client.batchGetItem(request), table)
    }
}

class UserOrganizationService(
    private val userOrganizationRepository: IUserOrganizationRepository
) {
    fun listByUser(userId: String): List<UserOrganization> {
        return userOrganizationRepository.listConfirmedByUserId(userId)
    }

    fun getByIdAndUser(id: String, userId: String): UserOrganization? {
        return userOrganizationRepository.getByOrganizationAndUser(id, userId)
    }
}


class OrganizationService(
    private val userRepository: IUserRepository,
    private val organizationRepository: IOrganizationRepository,
    private val userCollectionRepository: IUserCollectionRepository,
    private val userOrganizationRepository: IUserOrganizationRepository,
    private val vaultCollectionRepository: IVaultCollectionRepository,
    private val collectionCipherRepository: ICollectionCipherRepository
) {
    fun createOrganization(principal: KotwardenPrincipal, request: OrganizationCreateRequestModel): Organization {
        val organization = Organization()
        organization.id = ORGANIZATION_PREFIX + UUID.randomUUID().toString()
        organization.sk = organization.id
        organization.name = request.name
        organization.billingEmail = request.billingEmail
        organization.encryptedPrivateKey = request.keys!!.encryptedPrivateKey
        organization.publicKey = request.keys.publicKey
        organization.createdAt = OffsetDateTime.now()
        organization.updatedAt = OffsetDateTime.now()
        organizationRepository.save(organization)
        val userOrganization = UserOrganization()
        userOrganization.userId = principal.id
        userOrganization.organizationId = organization.id
        userOrganization.accessAll = true
        userOrganization.type = UserOrganization.Type.Owner
        userOrganization.status = UserOrganization.Status.Confirmed
        userOrganization.key = request.key
        userOrganization.createdAt = OffsetDateTime.now()
        userOrganization.updatedAt = OffsetDateTime.now()
        userOrganizationRepository.save(userOrganization)

        val vaultCollection = VaultCollection()
        vaultCollection.organizationId = organization.id
        vaultCollection.id = COLLECTION_PREFIX + UUID.randomUUID().toString()
        vaultCollection.name = request.collectionName
        vaultCollection.createdAt = OffsetDateTime.now()
        vaultCollection.updatedAt = OffsetDateTime.now()
        vaultCollectionRepository.save(vaultCollection)
        return organization
    }

    fun createCollection(organizationId: String, request: CollectionRequestModel) {
        vaultCollectionRepository.save(VaultCollection.converter.toModel(organizationId, request))
    }

    fun updateCipherCollections(cipherId: String, request: CipherCollectionsRequestModel) {
        collectionCipherRepository.updateCipherCollections(cipherId, request.collectionIds)
    }

    fun listByUserId(id: String): List<Pair<UserOrganization, Organization>> {
        val userOrganizations = userOrganizationRepository.listConfirmedByUserId(id)
        val userOrganizationsMap = mutableMapOf<String, UserOrganization>()
        userOrganizations.forEach {
            userOrganizationsMap[it.organizationId] = it
        }

        val organizations = organizationRepository.listByOrganizationIds(userOrganizations.map { it.organizationId })
        val list = mutableListOf<Pair<UserOrganization, Organization>>()
        organizations.forEach {
            list.add(userOrganizationsMap[it.id]!! to it)
        }
        return list
    }

    fun listCollectionByOrganization(organizationId: String): List<VaultCollection> {
        return vaultCollectionRepository.listByOrganization(organizationId)
    }

    fun listCollectionByUser(userId: String): List<VaultCollection> {
        val userCollections = userCollectionRepository.listByUserId(userId)
        return vaultCollectionRepository.listByCollectionIds(userCollections.map { it.collectionId })
    }

    fun listUserOrganizationsByOrganization(organizationId: String): List<Pair<UserOrganization, User>> {
        val userOrganizations = userOrganizationRepository.listByOrganization(organizationId)
        val users = userRepository.listByIds(userOrganizations.map { it.userId })
        return combine(userOrganizations, { uo -> uo.userId }, users, { u -> u.id })
    }
}