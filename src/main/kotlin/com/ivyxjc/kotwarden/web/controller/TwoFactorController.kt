package com.ivyxjc.kotwarden.web.controller

import com.ivyxjc.kotwarden.model.TwoFactor
import com.ivyxjc.kotwarden.web.kotwardenPrincipal
import com.ivyxjc.kotwarden.web.model.TwoFactorProviderResponseModelListResponseModel
import com.ivyxjc.kotwarden.web.service.TwoFactorService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class TwoFactorController(private val twoFactorService: TwoFactorService) {
    suspend fun twoFactor(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val list = twoFactorService.getByUser(principal.id)
            val resp = TwoFactorProviderResponseModelListResponseModel(
                xyObject = "list",
                data = list.map { TwoFactor.converter.toProviderResponse(it) }
            )
            ctx.respond(HttpStatusCode.OK, resp)
        }
    }
}