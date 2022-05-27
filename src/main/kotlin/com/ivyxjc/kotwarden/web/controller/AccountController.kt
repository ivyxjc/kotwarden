package com.ivyxjc.kotwarden.web.controller

import com.ivyxjc.kotwarden.web.model.RegisterRequest
import com.ivyxjc.kotwarden.web.service.AccountService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class AccountController(private val accountService: AccountService) {
    suspend fun login(ctx: ApplicationCall) {

    }

    suspend fun register(ctx: ApplicationCall) {
        ctx.receive<RegisterRequest>().apply {
            accountService.register(this)
            ctx.respond(HttpStatusCode.OK, Unit)
        }
    }

    suspend fun health(ctx: ApplicationCall) {
        ctx.apply {
        }
    }
}