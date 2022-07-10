package com.ivyxjc.kotwarden


import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

val dotenv = dotenv {
    directory = ".env"
    ignoreIfMalformed = true
    ignoreIfMissing = true
}

class Config {
    companion object {
        const val kdfIterations = 10000
        const val kdf = 0
        const val defaultValidityHours = 2L
        const val issuer = "issuer"
        const val audience = "bitwarden"
        val corsHost = System.getenv("CORS_HOST") ?: "*"
        val privateRsaKey: RSAPrivateKey = getPrivateKey() as RSAPrivateKey
        val publicRsaKey: RSAPublicKey = getPublicKey() as RSAPublicKey
        fun isSignupAllowed(email: String): Boolean {
            // todo check whether sign up is allowed or not
            return true
        }
    }
}

fun getPrivateKey(): PrivateKey {
    val key: String = Application::class.java.getResource("/credentials/private_key_pkcs8.pem")?.readText()
        ?: error("private key not found")
    val privateKeyPEM = key
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace(System.lineSeparator().toRegex(), "")
        .replace("-----END PRIVATE KEY-----", "")
    val encoded = Base64.getDecoder().decode(privateKeyPEM)
    val spec = PKCS8EncodedKeySpec(encoded)
    val kf: KeyFactory = KeyFactory.getInstance("RSA")
    return kf.generatePrivate(spec)
}

fun getPublicKey(): PublicKey {
    val key: String =
        Application::class.java.getResource("/credentials/public_key.pem")?.readText() ?: error("public key not found")
    val privateKeyPEM = key
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace(System.lineSeparator().toRegex(), "")
        .replace("-----END PUBLIC KEY-----", "")
    val encoded = Base64.getDecoder().decode(privateKeyPEM)
    val spec = X509EncodedKeySpec(encoded)
    val kf: KeyFactory = KeyFactory.getInstance("RSA")
    return kf.generatePublic(spec)
}