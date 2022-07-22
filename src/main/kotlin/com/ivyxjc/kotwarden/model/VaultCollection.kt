package com.ivyxjc.kotwarden.model

import com.ivyxjc.kotwarden.web.model.CollectionDetailsResponseModel
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
class VaultCollection {

    companion object {
        const val TABLE_NAME = "resource"
        val converter: VaullCollectionConverter = Mappers.getMapper(VaullCollectionConverter::class.java)
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    lateinit var organizationId: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    lateinit var id: String

    @get:DynamoDbAttribute("CreatedAt")
    lateinit var createdAt: OffsetDateTime

    @get:DynamoDbAttribute("UpdatedAt")
    lateinit var updatedAt: OffsetDateTime


    @get:DynamoDbAttribute("Name")
    lateinit var name: String
}

@Mapper
interface VaullCollectionConverter {
    @Mappings(
        Mapping(target = "xyObject", constant = "collection"),
    )
    fun toResponse(vaultCollection: VaultCollection): CollectionDetailsResponseModel


}