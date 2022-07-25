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
    authenticate("auth-jwt") {
        route("api/accounts") {
            get("profile") {
                accountController.profile(this.context)
            }
            post("kdf") {
                accountController.updateKdf(this.context)
            }
        }
    }
}

fun Routing.twofa(twoFactorController: TwoFactorController) {
    authenticate("auth-jwt") {
        route("api") {
            get("/two-factor") {
                twoFactorController.twoFactor(this.context)
            }
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

fun Routing.cipher(cipherController: CipherController, organizationController: OrganizationController) {

    authenticate("auth-jwt") {
        route("api/ciphers") {
            // Called when creating a new user-owned cipher.
            post("") {
                cipherController.createCipher(this.context)
            }

            post("create") {
                // Called when creating a new org-owned cipher, or cloning a cipher (whether
                // user- or org-owned). When cloning a cipher to a user-owned cipher,
                // `organizationId` is null.
                cipherController.createCipherRequest(this.context)
            }
            post("/admin") {
                cipherController.createCipherRequest(this.context)
            }
            post("{id}/share") {
                val id = this.context.parameters.getOrFail<String>("id")
                cipherController.shareCipher(id, this.context)
            }
            put("{id}/share") {
                val id = this.context.parameters.getOrFail<String>("id")
                cipherController.shareCipher(id, this.context)
            }
            get("{id}/admin") {
                val id = this.context.parameters.getOrFail<String>("id")
                cipherController.getCipher(id, this.context)
            }
            put("{id}/collections-admin") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.updateCipherCollections(id, this.context)
            }
            put("{id}/collections") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.updateCipherCollections(id, this.context)
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
            put("delete") {
                cipherController.deleteCiphers(this.context)
            }
            get("organization-details") {
                val parameters = this.context.request.queryParameters
                val organizationId = parameters.getOrFail<String>("organizationId")
                organizationController.listOrganizationDetail(organizationId, this.context)
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
            delete("{id}") {
                val id = this.context.parameters.getOrFail<String>("id")
                folderController.deleteFolder(this.context, id)
            }
            put("{id}") {
                val id = this.context.parameters.getOrFail<String>("id")
                folderController.updateFolder(this.context, id)
            }
            get("{id}") {
                val id = this.context.parameters.getOrFail<String>("id")
                TODO()
            }
        }
    }
}

fun Routing.organization(organizationController: OrganizationController) {
    authenticate("auth-jwt") {
        route("api/collections") {
            get("") {
                organizationController.listCollectionsByUser(this.context)
            }
        }
        route("api/plans") {
            get("") {
                organizationController.getPlans(this.context)
            }
            get("/") {
                organizationController.getPlans(this.context)
            }

        }
        route("api/organizations") {
            post("") {
                organizationController.createOrganization(this.context)
            }
            get("") {
                organizationController.listOrganizations(this.context)
            }
            get("{id}") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.getOrganization(id, this.context)
            }
            put("{id}") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.updateOrganization(id, this.context)
            }

            get("{id}/collections") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.listCollectionsByOrganization(id, this.context)
            }
            post("{id}/collections") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.createCollection(id, this.context)
            }
            get("{id}/users") {
                val id = this.context.parameters.getOrFail<String>("id")
                organizationController.listUsers(id, this.context)
            }
        }
    }
}