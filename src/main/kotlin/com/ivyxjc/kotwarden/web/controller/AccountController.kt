package com.ivyxjc.kotwarden.web.controller

import com.ivyxjc.kotwarden.model.User
import com.ivyxjc.kotwarden.web.kotwardenPrincipal
import com.ivyxjc.kotwarden.web.model.PreLoginRequest
import com.ivyxjc.kotwarden.web.model.RegisterRequest
import com.ivyxjc.kotwarden.web.service.AccountService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class AccountController(private val accountService: AccountService) {
    suspend fun preLogin(ctx: ApplicationCall) {
        ctx.receive<PreLoginRequest>().apply {
            val preLoginResponse = accountService.preLogin(this)
            ctx.respond(HttpStatusCode.OK, preLoginResponse)
        }

    }

    suspend fun register(ctx: ApplicationCall) {
        ctx.receive<RegisterRequest>().apply {
            accountService.register(this)
            ctx.respond(HttpStatusCode.OK, Unit)
        }
    }

    suspend fun profile(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val user = accountService.findById(principal.id)
            if (user == null) {
                ctx.respond(HttpStatusCode.NotFound, "Fail to find the user")
            }
            ctx.respond(HttpStatusCode.OK, User.converter.toProfileResponse(user!!))
        }
    }


}