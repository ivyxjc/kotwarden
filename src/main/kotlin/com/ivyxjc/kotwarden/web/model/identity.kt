package com.ivyxjc.kotwarden.web.model

@kotlinx.serialization.Serializable
data class IdentityConnectData(
    val grantType: String,
    val refreshToken: String? = null,
    val clientId: String? = null,
    val clientSecret: String? = null,
    val password: String? = null,
    val scope: String? = null,
    val username: String? = null,
    val deviceIdentifier: String? = null,
    val deviceName: String? = null,
    val deviceType: String? = null,
    val devicePushToken: String? = null,
    val twoFactorProvider: Int? = null,
    val twoFactorToken: String? = null,
    val twoFactorRemember: Int? = null
)


@kotlinx.serialization.Serializable
data class LoginResponse(
    val accessToken: String,
    val expiresIn: Long,
    val tokenType: String = "Bearer",
    val refreshToken: String?,
    val key: String?,
    val privateKey: String? = null,

    val kdf: Int?,
    val kdfIterations: Int?,
    val resetMasterPassword: Boolean = false,
    val scope: String?,
    val unofficialServer: Boolean = true
)