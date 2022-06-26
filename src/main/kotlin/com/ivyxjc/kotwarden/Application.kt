package com.ivyxjc

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ivyxjc.kotwarden.Config
import com.ivyxjc.kotwarden.ModuleConfig
import com.ivyxjc.kotwarden.plugins.account
import com.ivyxjc.kotwarden.plugins.cipher
import com.ivyxjc.kotwarden.plugins.identity
import com.ivyxjc.kotwarden.web.controller.AccountController
import com.ivyxjc.kotwarden.web.controller.IdentityController
import com.ivyxjc.kotwarden.web.controller.SyncController
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.kodein.di.instance


fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.main() {
    val accountController by ModuleConfig.kodein.instance<AccountController>()
    val identityController by ModuleConfig.kodein.instance<IdentityController>()
    val cipherController by ModuleConfig.kodein.instance<SyncController>()
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            this@main.log.error("inner error", cause)
            call.respondText(text = cause.message ?: "", status = HttpStatusCode.InternalServerError)
            throw cause
        }
    }
    install(Routing) {
        account(accountController)
        identity(identityController)
        cipher(cipherController)
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT
                    .require(Algorithm.RSA256(Config.publicRsaKey, Config.privateRsaKey))
                    .withAudience(Config.audience)
                    .withIssuer(Config.issuer)
                    .build()
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