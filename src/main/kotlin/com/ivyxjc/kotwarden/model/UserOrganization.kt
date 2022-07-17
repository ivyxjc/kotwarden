package com.ivyxjc.kotwarden.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.OffsetDateTime

@DynamoDbBean
class UserOrganization {

    companion object {
        const val TABLE_NAME = "user_organization"
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("UserId")
    lateinit var userId: String


    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("OrganizationId")
    lateinit var organizationId: String

    @get:DynamoDbAttribute("CreatedAt")
    lateinit var createdAt: OffsetDateTime

    @get:DynamoDbAttribute("UpdatedAt")
    lateinit var updatedAt: OffsetDateTime


}