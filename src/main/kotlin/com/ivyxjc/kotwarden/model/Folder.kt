package com.ivyxjc.kotwarden.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.time.OffsetDateTime

@DynamoDbBean
class Folder {

    companion object {
        const val TABLE_NAME = "folder"
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

    @get:DynamoDbAttribute("Name")
    lateinit var name: String
}