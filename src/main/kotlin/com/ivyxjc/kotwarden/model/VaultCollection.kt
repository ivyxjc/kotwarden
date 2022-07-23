package com.ivyxjc.kotwarden.model

import com.ivyxjc.kotwarden.web.model.CollectionDetailsResponseModel
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*
import java.time.OffsetDateTime

@DynamoDbBean
class VaultCollection {

    companion object {
        const val TABLE_NAME = "resource"
        const val SK_INDEX = "SK-Index"
        val converter: VaultCollectionConverter = Mappers.getMapper(VaultCollectionConverter::class.java)
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    lateinit var organizationId: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    @get:DynamoDbSecondaryPartitionKey(indexNames = [SK_INDEX])
    lateinit var id: String

    @get:DynamoDbAttribute("CreatedAt")
    lateinit var createdAt: OffsetDateTime

    @get:DynamoDbAttribute("UpdatedAt")
    lateinit var updatedAt: OffsetDateTime


    @get:DynamoDbAttribute("Name")
    lateinit var name: String
}

@Mapper
interface VaultCollectionConverter {
    @Mappings(
        Mapping(target = "xyObject", constant = "collection"),
    )
    fun toResponse(vaultCollection: VaultCollection): CollectionDetailsResponseModel


}