package com.ivyxjc.kotwarden.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*

@DynamoDbBean
class CollectionCipher {
    companion object {
        const val TABLE_NAME = "collection_cipher"
        const val REVERSE_INDEX = "Reverse-Index"
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("CollectionId")
    @get:DynamoDbSecondarySortKey(indexNames = [REVERSE_INDEX])
    lateinit var collectionId: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("CipherId")
    @get:DynamoDbSecondaryPartitionKey(indexNames = [REVERSE_INDEX])
    lateinit var cipherId: String

}