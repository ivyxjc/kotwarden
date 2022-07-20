package com.ivyxjc.kotwarden.model

import com.ivyxjc.kotwarden.web.model.OrganizationResponseModel
import com.ivyxjc.kotwarden.web.model.ProfileOrganizationResponseModel
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.OffsetDateTime

@DynamoDbBean
class Organization {
    companion object {
        const val TABLE_NAME = "resource"
        val converter: OrganizationConverter = Mappers.getMapper(OrganizationConverter::class.java)
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    lateinit var id: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    lateinit var sk: String

    @get:DynamoDbAttribute("CreatedAt")
    lateinit var createdAt: OffsetDateTime

    @get:DynamoDbAttribute("UpdatedAt")
    lateinit var updatedAt: OffsetDateTime

    @get:DynamoDbAttribute("Name")
    lateinit var name: String

    @get:DynamoDbAttribute("BillingEmail")
    lateinit var billingEmail: String

    @get:DynamoDbAttribute("PrivateKey")
    var encryptedPrivateKey: String? = null

    @get:DynamoDbAttribute("PublicKey")
    var publicKey: String? = null
}

@Mapper
interface OrganizationConverter {
    @Mappings(
        Mapping(target = "xyObject", constant = "organization"),
    )
    fun toResponse(organization: Organization): OrganizationResponseModel

    fun toProfileResponse(organization: OrganizationResponseModel): ProfileOrganizationResponseModel

}