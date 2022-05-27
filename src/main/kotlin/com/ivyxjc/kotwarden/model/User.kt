package com.ivyxjc.kotwarden.model

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
        const val KDF_TYPE = 0
        const val KDF_ITERATIONS = 100000
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
            field = hashPassword(masterPasswordHash!!, this.salt, KDF_ITERATIONS)
        }

    @get:DynamoDbAttribute("Salt")
    var salt: ByteArray = ByteArray(32)

    @get:DynamoDbAttribute("Kdf")
    var kdf: Int = KDF_TYPE

    @get:DynamoDbAttribute("KdfIterations")
    var kdfIterations: Int = KDF_ITERATIONS

    init {
        SecureRandom().nextBytes(this.salt)
    }
}

@Mapper
interface UserConverter {
    @Mappings(
        Mapping(source = "keys.encryptedPrivateKey", target = "encryptedPrivateKey"),
        Mapping(source = "keys.publicKey", target = "publicKey")
    )
    fun toModel(registerReq: RegisterRequest): User
}