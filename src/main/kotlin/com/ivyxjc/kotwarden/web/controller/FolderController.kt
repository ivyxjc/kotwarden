package com.ivyxjc.kotwarden.web.controller

import com.ivyxjc.kotwarden.web.kotwardenPrincipal
import com.ivyxjc.kotwarden.web.model.FolderRequestModel
import com.ivyxjc.kotwarden.web.service.FolderService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class FolderController(val folderService: FolderService) {
    suspend fun createFolder(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<FolderRequestModel>()
            this.respond(folderService.createFolder(principal, request))
        }
    }
}