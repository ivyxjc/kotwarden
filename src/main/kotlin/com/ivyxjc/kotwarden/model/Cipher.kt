package com.ivyxjc.kotwarden.model

import com.ivyxjc.kotwarden.util.EMPTY_STRING
import com.ivyxjc.kotwarden.web.model.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mapstruct.InjectionStrategy
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
class Cipher {
    companion object {
        const val TABLE_NAME = "cipher"
        val converter: CipherConverter = Mappers.getMapper(CipherConverter::class.java)
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("UserId")
    lateinit var userId: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("Id")
    lateinit var id: String

    @get:DynamoDbAttribute("CreatedAt")
    lateinit var createdAt: OffsetDateTime

    @get:DynamoDbAttribute("UpdatedAt")
    lateinit var updatedAt: OffsetDateTime


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

    @get:DynamoDbAttribute("favorite")
    var favorite: Boolean = false
}

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@JvmDefaultWithCompatibility
interface CipherConverter {
    fun mapField(field: String?): List<CipherFieldModel>? {
        println(field)
        if (field == null || field.isEmpty()) {
            return null
        }
        return Json.decodeFromString<List<CipherFieldModel>>(field)
    }

    fun mapField(list: List<CipherFieldModel>?): String? {
        return Json.encodeToString(list)
    }

    fun mapData(data: String?): Map<String, String>? {
        return mapOf()
    }

    fun mapData(map: Map<String, String>?): String? {
        return EMPTY_STRING
    }

    fun mapPasswordHistory(data: String?): List<CipherPasswordHistoryModel> {
        return listOf()
    }

    fun mapPasswordHistory(data: List<CipherPasswordHistoryModel>?): String? {
        return EMPTY_STRING
    }

    fun toModel(request: CipherRequestModel): Cipher

    fun toResponse(cipher: Cipher): CipherResponseModel

    fun toResponse(cipher: Cipher, requestModel: CipherRequestModel): CipherResponseModel {
        val resp = toResponse(cipher)
        resp.fields = requestModel.fields
        resp.login = requestModel.login
        resp.identity = requestModel.identity
        resp.card = requestModel.card
        resp.secureNote = requestModel.secureNote
        resp.xObject = "cipher"
        return resp
    }

    @Mappings(
        Mapping(target = "xyObject", constant = "cipherDetails"),
        Mapping(target = "data", ignore = true),
        Mapping(target = "edit", constant = "true"),
        Mapping(target = "viewPassword", constant = "true"),
    )
    fun toCipherDetailResponse(cipher: Cipher): CipherDetailsResponseModel
}
