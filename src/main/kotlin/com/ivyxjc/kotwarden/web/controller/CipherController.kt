package com.ivyxjc.kotwarden.web.controller

import com.ivyxjc.kotwarden.util.isEmpty
import com.ivyxjc.kotwarden.util.isNotEmpty
import com.ivyxjc.kotwarden.web.kError
import com.ivyxjc.kotwarden.web.kotwardenPrincipal
import com.ivyxjc.kotwarden.web.model.CipherBulkDeleteRequestModel
import com.ivyxjc.kotwarden.web.model.CipherCreateRequestModel
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


    /**
     * Called when creating a new org-owned cipher, or cloning a cipher (whether
     * user- or org-owned). When cloning a cipher to a user-owned cipher,
     * `organizationId` is null.
     */
    suspend fun createCipherRequest(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<CipherCreateRequestModel>()
            if (isNotEmpty(request.cipher.organizationId) && isEmpty(request.collectionIds)) {
                kError("You must select at least one collection.")
            }
            this.respond(cipherService.createShareCipher(principal, request))
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