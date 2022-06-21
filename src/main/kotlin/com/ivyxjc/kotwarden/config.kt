package com.ivyxjc.kotwarden


import io.github.cdimascio.dotenv.dotenv

val dotenv = dotenv {
    directory = ".env"
    ignoreIfMalformed = true
    ignoreIfMissing = true
}

class Config {
    companion object {
        const val kdfIterations = 100000
        const val kdf = 0
        fun isSignupAllowed(email: String): Boolean {
            // todo check whether sign up is allowed or not
            return true
        }

    }

}