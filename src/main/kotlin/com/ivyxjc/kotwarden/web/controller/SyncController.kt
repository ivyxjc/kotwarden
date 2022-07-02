package com.ivyxjc.kotwarden.web.controller

import com.ivyxjc.kotwarden.web.kotwardenPrincipal
import com.ivyxjc.kotwarden.web.service.SyncService
import io.ktor.server.application.*
import io.ktor.server.response.*

class SyncController(private val syncService: SyncService) {

    suspend fun sync(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            this.respond(syncService.sync(principal, principal.id))
        }
    }

}