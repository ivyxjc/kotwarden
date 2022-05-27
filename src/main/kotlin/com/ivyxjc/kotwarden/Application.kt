package com.ivyxjc

import com.ivyxjc.kotwarden.ModuleConfig
import com.ivyxjc.kotwarden.plugins.account
import com.ivyxjc.kotwarden.web.controller.AccountController
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.kodein.di.instance


fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.main() {
    val accountController by ModuleConfig.kodein.instance<AccountController>()
    install(Routing) {
        account(accountController)
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}