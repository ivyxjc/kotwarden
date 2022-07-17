package com.ivyxjc.kotwarden.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.OffsetDateTime

@DynamoDbBean
class Organization {
    companion object {
        const val TABLE_NAME = "resource"
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