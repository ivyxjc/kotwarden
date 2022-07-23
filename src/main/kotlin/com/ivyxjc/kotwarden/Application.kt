package com.ivyxjc

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ivyxjc.kotwarden.Config
import com.ivyxjc.kotwarden.ModuleConfig
import com.ivyxjc.kotwarden.plugins.*
import com.ivyxjc.kotwarden.web.controller.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.kodein.di.instance


fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.main() {
    val accountController by ModuleConfig.kodein.instance<AccountController>()
    val identityController by ModuleConfig.kodein.instance<IdentityController>()
    val syncController by ModuleConfig.kodein.instance<SyncController>()
    val cipherController by ModuleConfig.kodein.instance<CipherController>()
    val folderController by ModuleConfig.kodein.instance<FolderController>()
    val organizationController by ModuleConfig.kodein.instance<OrganizationController>()
    install(CORS) {
        allowHost(Config.corsHost)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        listOf("auth-email", "bitwarden-client-name", "bitwarden-client-version", "device-type", "pragma").forEach {
            allowHeader(it)
        }
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.CacheControl)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            this@main.log.error("inner error", cause)
            call.respondText(text = cause.message ?: "", status = HttpStatusCode.InternalServerError)
            throw cause
        }
    }
    install(Routing) {
        health()
        account(accountController)
        identity(identityController)
        sync(syncController)
        cipher(cipherController, organizationController)
        folder(folderController)
        organization(organizationController)
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.RSA256(Config.publicRsaKey, Config.privateRsaKey)).withAudience(Config.audience)
                    .withIssuer(Config.issuer).build()
            )
            validate { credentials ->
                if (credentials.payload.getClaim("id").asString() != "") {
                    JWTPrincipal(credentials.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }

    }
}