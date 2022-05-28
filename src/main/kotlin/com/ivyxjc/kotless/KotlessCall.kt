package com.ivyxjc.kotless

import io.kotless.dsl.ktor.app.KotlessResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*

class KotlessCall(application: Application, request: HttpRequest) : BaseApplicationCall(application) {
    override val request = KotlessRequest(request, this)
    override val response = KotlessResponse(this)

    override val parameters: Parameters by lazy { this.request.queryParameters }

    init {
        putResponseAttribute()
    }
}
