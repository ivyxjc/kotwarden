package com.ivyxjc.kotwarden.web.controller

import com.ivyxjc.kotwarden.web.kotwardenPrincipal
import com.ivyxjc.kotwarden.web.model.CipherBulkDeleteRequestModel
import com.ivyxjc.kotwarden.web.model.CipherRequestModel
import com.ivyxjc.kotwarden.web.model.ImportCiphersRequestModel
import com.ivyxjc.kotwarden.web.service.CipherService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class CipherController(private val cipherService: CipherService) {

    suspend fun createCipher(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<CipherRequestModel>()
            this.respond(cipherService.createCipher(principal, request))
        }
    }

    suspend fun deleteCipher(ctx: ApplicationCall, id: String) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            this.respond(cipherService.deleteCipher(principal, id))
        }
    }

    suspend fun deleteCiphers(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<CipherBulkDeleteRequestModel>()
            cipherService.deleteCiphers(principal, request)
            this.respond(HttpStatusCode.OK)
        }
    }

    suspend fun updateCipher(ctx: ApplicationCall, cipherId: String) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<CipherRequestModel>()
            this.respond(cipherService.updateCipher(principal, cipherId, request))
        }
    }

    suspend fun importCiphers(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<ImportCiphersRequestModel>()
            cipherService.importCiphers(principal, request)
            this.respond(HttpStatusCode.OK)
        }
    }
}