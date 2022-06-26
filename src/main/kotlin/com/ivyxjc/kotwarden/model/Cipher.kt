package com.ivyxjc.kotwarden.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.time.OffsetDateTime

@DynamoDbBean
class Cipher {
    companion object {
        const val TABLE_NAME = "cipher"
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("Id")
    lateinit var id: String

    @get:DynamoDbAttribute("CreatedAt")
    lateinit var createdAt: OffsetDateTime

    @get:DynamoDbAttribute("UpdatedAt")
    lateinit var updatedAt: OffsetDateTime

    @get:DynamoDbAttribute("UserId")
    lateinit var userId: String

    @get:DynamoDbAttribute("OrganizationId")
    var organizationId: String? = null


    /*
        Login = 1,
        SecureNote = 2,
        Card = 3,
        Identity = 4
    */
    @get:DynamoDbAttribute("type")
    var type: Int = 0

    @get:DynamoDbAttribute("name")
    lateinit var name: String

    @get:DynamoDbAttribute("notes")
    var notes: String? = null

    @get:DynamoDbAttribute("fields")
    var fields: String? = null

    @get:DynamoDbAttribute("data")
    lateinit var data: String

    @get:DynamoDbAttribute("passwordHistory")
    var passwordHistory: String? = null

    @get:DynamoDbAttribute("deletedAt")
    var deletedAt: OffsetDateTime? = null

    @get:DynamoDbAttribute("reprompt")
    var reprompt: Int? = null

    // cipher folder
    @get:DynamoDbAttribute("FolderId")
    var folderId: String? = null
}