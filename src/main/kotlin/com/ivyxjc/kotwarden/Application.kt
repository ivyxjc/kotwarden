package com.ivyxjc

import com.ivyxjc.kotwarden.ModuleConfig
import com.ivyxjc.kotwarden.plugins.account
import com.ivyxjc.kotwarden.plugins.identity
import com.ivyxjc.kotwarden.web.controller.AccountController
import com.ivyxjc.kotwarden.web.controller.IdentityController
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
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
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = cause.message ?: "", status = HttpStatusCode.InternalServerError)
            print("")
            throw cause
        }
    }
    install(Routing) {
        account(accountController)
        identity(identityController)
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}