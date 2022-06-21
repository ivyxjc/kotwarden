package com.ivyxjc.kotwarden.model

import com.ivyxjc.kotwarden.Config
import com.ivyxjc.kotwarden.util.hashPassword
import com.ivyxjc.kotwarden.web.model.RegisterRequest
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.security.SecureRandom
import java.util.*

@DynamoDbBean
class User {
    companion object {
        const val TABLE_NAME = "user"
        val converter: UserConverter = Mappers.getMapper(UserConverter::class.java)
    }

    constructor()

    constructor(email: String, id: UUID, masterPasswordHash: String?) {
        this.email = email
        this.id = id
        this.masterPasswordHint = masterPasswordHash
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("Email")
    lateinit var email: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("Id")
    lateinit var id: UUID

    @get:DynamoDbAttribute("Name")
    lateinit var name: String

    @get:DynamoDbAttribute("Token")
    var token: String? = null

    @get:DynamoDbAttribute("Key")
    var key: String? = null

    @get:DynamoDbAttribute("PrivateKey")
    var encryptedPrivateKey: String? = null

    @get:DynamoDbAttribute("PublicKey")
    var publicKey: String? = null

    @get:DynamoDbAttribute("MasterPasswordHint")
    var masterPasswordHint: String? = null

    @get:DynamoDbAttribute("MasterPasswordHash")
    var masterPasswordHash: String? = null
        set(masterPasswordHash) {
            field = hashPassword(masterPasswordHash!!, this.salt, Config.kdfIterations)
        }

    @get:DynamoDbAttribute("Salt")
    var salt: ByteArray = ByteArray(32)

    @get:DynamoDbAttribute("Kdf")
    var kdf: Int = Config.kdf

    @get:DynamoDbAttribute("KdfIterations")
    var kdfIterations: Int = Config.kdfIterations


    // TODO: 2022/6/21 default from config 
    @get:DynamoDbAttribute("enabled")
    var enabled: Boolean = true

    init {
        SecureRandom().nextBytes(this.salt)
    }
}

@Mapper
interface UserConverter {
    /**
     * missing required property: id
     */
    @Mappings(
        Mapping(source = "keys.encryptedPrivateKey", target = "encryptedPrivateKey"),
        Mapping(source = "keys.publicKey", target = "publicKey")
    )
    fun toModel(registerReq: RegisterRequest): User
}