package com.ivyxjc.kotwarden.web.controller

import com.ivyxjc.kotwarden.model.Folder
import com.ivyxjc.kotwarden.web.kotwardenPrincipal
import com.ivyxjc.kotwarden.web.model.FolderRequestModel
import com.ivyxjc.kotwarden.web.service.FolderService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class FolderController(val folderService: FolderService) {
    suspend fun createFolder(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<FolderRequestModel>()
            ctx.respond(
                HttpStatusCode.OK,
                Folder.converter.toFolderResponse(folderService.createFolder(principal, request))
            )
        }
    }

    suspend fun deleteFolder(ctx: ApplicationCall, id: String) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            this.respond(folderService.deleteFolder(principal, id))
            ctx.respond(HttpStatusCode.OK)
        }
    }


    suspend fun updateFolder(ctx: ApplicationCall, id: String) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<FolderRequestModel>()
            this.respond(HttpStatusCode.OK, folderService.updateFolder(principal, id, request))
        }
    }

    suspend fun getFolder(ctx: ApplicationCall, id: String) {

        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<FolderRequestModel>()
            this.respond(Folder.converter.toFolderResponse(folderService.createFolder(principal, request)))
        }
    }
}