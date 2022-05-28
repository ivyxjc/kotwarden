package com.ivyxjc.kotwarden


import io.github.cdimascio.dotenv.dotenv

val dotenv = dotenv {
    directory = ".env"
    ignoreIfMalformed = true
    ignoreIfMissing = true
}

val awsAccessKey = dotenv["AWS_ACCESS_KEY_ID"]!!
val awsAccessSecret = dotenv["AWS_SECRET_ACCESS_KEY"]!!
