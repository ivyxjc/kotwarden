package com.ivyxjc.kotwarden.plugins

import com.ivyxjc.kotwarden.web.controller.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Routing.health() {
    route("api") {
        get("health") {
            this.context.respond(HttpStatusCode.OK, "OK")
        }
    }
}

fun Routing.account(accountController: AccountController) {
    route("api/accounts") {
        post("register") {
            accountController.register(this.context)
        }
        post("prelogin") {
            accountController.preLogin(this.context)
        }
    }
}

fun Routing.identity(identityController: IdentityController) {
    route("identity") {
        post("connect/token") {
            identityController.login(this.context)
        }
    }
}

fun Routing.sync(syncController: SyncController) {
    authenticate("auth-jwt") {
        get("api/sync") {
            syncController.sync(this.context)
        }
    }
}

fun Routing.cipher(cipherController: CipherController) {

    authenticate("auth-jwt") {
        route("api/ciphers") {
            // Called when creating a new user-owned cipher.
            post("") {
                cipherController.createCipher(this.context)
            }
            put("{id}") {
                val id = this.context.parameters.getOrFail<String>("id")
                cipherController.updateCipher(this.context, id)
            }
            post("import") {
                cipherController.importCiphers(this.context)
            }
            put("{id}/delete") {
                val id = this.context.parameters.getOrFail<String>("id")
                cipherController.deleteCipher(this.context, id)
            }
        }
    }
}

fun Routing.folder(folderController: FolderController) {
    authenticate("auth-jwt") {
        route("api/folders") {
            post("") {
                folderController.createFolder(this.context)
            }
        }
    }
}

//
//fun Route.AccountsApi() {
//    val gson = Gson()
//    val empty = mutableMapOf<String, Any?>()
//
//    route("/accounts/api-key") {
//        post {
//            val exampleContentType = "application/json"
//            val exampleContentString = """{
//              "apiKey" : "apiKey",
//              "object" : "object"
//            }"""
//
//            when(exampleContentType) {
//                "application/json" -> call.respond(gson.fromJson(exampleContentString, empty::class.java))
//                "application/xml" -> call.respondText(exampleContentString, ContentType.Text.Xml)
//                else -> call.respondText(exampleContentString)
//            }
//        }
//    }
//
//
//    get<Paths.accountsBillingGet> {
//    }
//
//
//    route("/accounts/cancel-premium") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    delete<Paths.accountsDelete> {  _: Paths.accountsDelete ->
//        call.respond(HttpStatusCode.NotImplemented)
//    }
//
//
//    route("/accounts/delete") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    route("/accounts/delete-recover") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    route("/accounts/delete-recover-token") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    route("/accounts/email") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    route("/accounts/email-token") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    get<Paths.accountsEnterprisePortalSigninTokenGet> {  _: Paths.accountsEnterprisePortalSigninTokenGet ->
//        call.respond(HttpStatusCode.NotImplemented)
//    }
//
//
//    route("/accounts/iap-check") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    route("/accounts/kdf") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    route("/accounts/key") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    get<Paths.accountsKeysGet> {  _: Paths.accountsKeysGet ->
//        val exampleContentType = "application/json"
//        val exampleContentString = """{
//          "privateKey" : "privateKey",
//          "publicKey" : "publicKey",
//          "key" : "key",
//          "object" : "object"
//        }"""
//
//        when(exampleContentType) {
//            "application/json" -> call.respond(gson.fromJson(exampleContentString, empty::class.java))
//            "application/xml" -> call.respondText(exampleContentString, ContentType.Text.Xml)
//            else -> call.respondText(exampleContentString)
//        }
//    }
//
//
//    route("/accounts/keys") {
//        post {
//            val exampleContentType = "application/json"
//            val exampleContentString = """{
//              "privateKey" : "privateKey",
//              "publicKey" : "publicKey",
//              "key" : "key",
//              "object" : "object"
//            }"""
//
//            when(exampleContentType) {
//                "application/json" -> call.respond(gson.fromJson(exampleContentString, empty::class.java))
//                "application/xml" -> call.respondText(exampleContentString, ContentType.Text.Xml)
//                else -> call.respondText(exampleContentString)
//            }
//        }
//    }
//
//
//    route("/accounts/license") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    get<Paths.accountsOrganizationsGet> {
//    }
//
//
//    route("/accounts/password-hint") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    route("/accounts/password") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    route("/accounts/payment") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    route("/accounts/prelogin") {
//        post {
//
//        }
//    }
//
//
//    route("/accounts/premium") {
//        post {
//
//        }
//    }
//
//
//    get<Paths.accountsProfileGet> {
//    }
//
//
//    route("/accounts/profile") {
//        post {
//
//        }
//    }
//
//
//    route("/accounts/profile") {
//        put {
//
//        }
//    }
//
//
//
//
//
//    route("/accounts/reinstate-premium") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    get<Paths.accountsRevisionDateGet> {  _: Paths.accountsRevisionDateGet ->
//        call.respond(HttpStatusCode.NotImplemented)
//    }
//
//
//    route("/accounts/rotate-api-key") {
//        post {
//            val exampleContentType = "application/json"
//            val exampleContentString = """{
//              "apiKey" : "apiKey",
//              "object" : "object"
//            }"""
//
//            when(exampleContentType) {
//                "application/json" -> call.respond(gson.fromJson(exampleContentString, empty::class.java))
//                "application/xml" -> call.respondText(exampleContentString, ContentType.Text.Xml)
//                else -> call.respondText(exampleContentString)
//            }
//        }
//    }
//
//
//    route("/accounts/security-stamp") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    route("/accounts/set-password") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    delete<Paths.accountsSsoOrganizationIdDelete> {  _: Paths.accountsSsoOrganizationIdDelete ->
//        call.respond(HttpStatusCode.NotImplemented)
//    }
//
//
//    get<Paths.accountsSsoUserIdentifierGet> {  _: Paths.accountsSsoUserIdentifierGet ->
//        call.respond(HttpStatusCode.NotImplemented)
//    }
//
//
//    route("/accounts/storage") {
//
//
//    }
//
//
//
//    route("/accounts/tax") {
//        put {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    route("/accounts/verify-email") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    route("/accounts/verify-email-token") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//
//    route("/accounts/verify-password") {
//        post {
//            call.respond(HttpStatusCode.NotImplemented)
//        }
//    }
//
//}
