package com.ivyxjc.kotwarden.web.controller

import com.ivyxjc.kotwarden.web.model.IdentityConnectData
import com.ivyxjc.kotwarden.web.service.IIdentityService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class IdentityController(private val identityService: IIdentityService) {
    suspend fun login(ctx: ApplicationCall) {
        ctx.receive<Parameters>().apply {
            val connectData = IdentityConnectData(
                grantType = this["grant_type"]!!,
                refreshToken = this["refresh_token"],
                scope = this["scope"],
                clientId = this["client_id"],
                password = this["password"],
                username = this["username"],
                deviceIdentifier = this["deviceIdentifier"],
                deviceName = this["deviceName"],
                deviceType = this["deviceType"],
                clientSecret = this["clientSecret"]
            )
            val loginResponse = when (connectData.grantType) {
                "refresh_token" -> {
                    check(connectData.refreshToken, "refresh_token cannot be blank")
                    identityService.refreshToken(connectData)
                }
                "password" -> {
                    check(connectData.clientId, "client_id cannot be blank")
                    check(connectData.password, "password cannot be blank")
                    check(connectData.scope, "")
                    check(connectData.username, "")

                    check(connectData.deviceIdentifier, "")
                    check(connectData.deviceName, "")
                    check(connectData.deviceType, "")
                    identityService.passwordLogin(connectData)
                }
                "client_credentials" -> {
                    check(connectData.clientId, "")
                    check(connectData.clientSecret, "")
                    check(connectData.scope, "")
                    identityService.apiKeyLogin(connectData)
                }
                else -> {
                    error("Invalid scope")
                }
            }
            ctx.respond(HttpStatusCode.OK, loginResponse)
        }
    }

    private fun check(value: String?, msg: String) {

    }
}