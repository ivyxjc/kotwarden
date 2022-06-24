package com.ivyxjc.kotwarden


import io.github.cdimascio.dotenv.dotenv
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

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
        private val keyPairGen = KeyPairGenerator.getInstance("RSA")
        private val keyPair = keyPairGen.genKeyPair()

        val privateRsaKey: RSAPrivateKey = keyPair.private as RSAPrivateKey
        val publicRsaKey: RSAPublicKey = keyPair.public as RSAPublicKey
        fun isSignupAllowed(email: String): Boolean {
            // todo check whether sign up is allowed or not
            return true
        }
    }
}

