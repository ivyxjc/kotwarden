@file:JvmName(name = "Server")

package com.ivyxjc.kotwarden

import com.ivyxjc.kotless.KotlessAWS
import com.ivyxjc.kotwarden.plugins.account
import com.ivyxjc.kotwarden.web.controller.AccountController
import com.ivyxjc.kotwarden.web.service.UserRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.kodein.di.instance

@Suppress("unused")
class Server : KotlessAWS() {
    /**
     * lambda aws sdk client warm up
     * since aws sdk v2 dynamodb client first request is too slow
     */
    init {
        val repo by ModuleConfig.kodein.instance<UserRepository>()
        repo.findByUser("sample@example.com")
    }

    override fun prepare(app: Application) {
        val accountController by ModuleConfig.kodein.instance<AccountController>()
        app.install(Routing) {
            account(accountController)
        }
        app.install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
}
