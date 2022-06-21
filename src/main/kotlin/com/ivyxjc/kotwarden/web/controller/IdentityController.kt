package com.ivyxjc.kotwarden.web.controller

import com.ivyxjc.kotwarden.web.model.IdentityConnectData
import com.ivyxjc.kotwarden.web.service.IIdentityService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class IdentityController(private val identityService: IIdentityService) {
    suspend fun login(ctx: ApplicationCall) {
        ctx.receive<IdentityConnectData>().apply {
            val preLoginResponse = when (this.scope) {
                "refresh_token" -> {
                    check(this.refreshToken, "refresh_token cannot be blank")
                    identityService.refreshToken(this)
                }
                "password" -> {
                    check(this.clientId, "client_id cannot be blank")
                    check(this.password, "password cannot be blank")
                    check(this.scope, "")
                    check(this.username, "")

                    check(this.deviceIdentifier, "")
                    check(this.deviceName, "")
                    check(this.deviceType, "")
                    identityService.passwordLogin(this)
                }
                "client_credentials" -> {
                    check(this.clientId, "")
                    check(this.clientSecret, "")
                    check(this.scope, "")
                    identityService.apiKeyLogin(this)
                }
                else -> {
                    error("Invalid scope")
                }
            }
            ctx.respond(HttpStatusCode.OK, preLoginResponse)
        }
    }

    private fun check(value: String?, msg: String) {

    }
}