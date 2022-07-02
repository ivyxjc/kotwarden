package com.ivyxjc.kotwarden.web.controller

import com.ivyxjc.kotwarden.web.kotwardenPrincipal
import com.ivyxjc.kotwarden.web.model.CipherRequestModel
import com.ivyxjc.kotwarden.web.service.CipherService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class CipherController(private val cipherService: CipherService) {

    suspend fun createCiphers(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<CipherRequestModel>()
            this.respond(cipherService.createCipher(principal, request))
        }

    }
}