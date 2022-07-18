package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.Organization
import com.ivyxjc.kotwarden.model.UserOrganization
import com.ivyxjc.kotwarden.model.VaultCollection
import com.ivyxjc.kotwarden.util.COLLECTION_PREFIX
import com.ivyxjc.kotwarden.util.ORGANIZATION_PREFIX
import com.ivyxjc.kotwarden.web.model.KotwardenPrincipal
import com.ivyxjc.kotwarden.web.model.OrganizationCreateRequestModel
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.time.OffsetDateTime
import java.util.*

interface IUserOrganizationRepository {
    fun save(userOrganization: UserOrganization)

}

interface IOrganizationRepository {
    fun save(organization: Organization)
}

class UserOrganizationRepository(private val client: DynamoDbEnhancedClient) : IUserOrganizationRepository {
    private val schema = TableSchema.fromBean(UserOrganization::class.java)
    private val table = client.table(UserOrganization.TABLE_NAME, schema)

    override fun save(userOrganization: UserOrganization) {
        table.putItem(userOrganization)
    }

}

class OrganizationRepository(private val client: DynamoDbEnhancedClient) : IOrganizationRepository {

    private val schema = TableSchema.fromBean(Organization::class.java)
    private val table = client.table(Organization.TABLE_NAME, schema)


    override fun save(organization: Organization) {
        table.putItem(organization)
    }


}

class OrganizationService(
    private val organizationRepository: IOrganizationRepository,
    private val userOrganizationRepository: IUserOrganizationRepository,
    private val vaultCollectionRepository: IVaultCollectionRepository
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

}