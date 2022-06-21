package com.ivyxjc.kotwarden.web.model

import com.ivyxjc.kotwarden.model.serializer.UUIDSerializer
import kotlinx.serialization.json.JsonElement
import java.util.*

//region register
@kotlinx.serialization.Serializable
data class RegisterRequest(
    val email: String,
    val masterPasswordHash: String,
    val masterPasswordHint: String? = null,
    val name: String,
    val key: String? = null,
    val keys: KeysRequest? = null,
    // user invitation token
    val token: String? = null,
    @kotlinx.serialization.Serializable(with = UUIDSerializer::class) val organizationUserId: UUID? = null,
    val kdf: Int? = null,
    val kdfIterations: Int? = null,
    val referenceData: Map<String, JsonElement>? = null
)

@kotlinx.serialization.Serializable
data class KeysRequest(
    val encryptedPrivateKey: String, val publicKey: String? = null
)
//endregion

//region pre login
@kotlinx.serialization.Serializable
data class PreLoginRequest(
    val email: String
)

@kotlinx.serialization.Serializable
data class PreLoginResponse(
    val kdf: Int? = null,
    val kdfIterations: Int? = null
)
//endregion