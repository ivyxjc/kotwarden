package com.ivyxjc.kotwarden.web


class NotAuthorizedException : RuntimeException {
    constructor(message: String, ex: Exception?) : super(message, ex)
    constructor(message: String) : super(message)
    constructor(ex: Exception) : super(ex)
}

fun notAuthorized(message: Any): Nothing = throw NotAuthorizedException(message.toString())
