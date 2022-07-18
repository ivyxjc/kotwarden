package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.Cipher
import com.ivyxjc.kotwarden.model.Folder
import com.ivyxjc.kotwarden.model.User
import com.ivyxjc.kotwarden.util.decodeFromString
import com.ivyxjc.kotwarden.util.format
import com.ivyxjc.kotwarden.web.model.KotwardenPrincipal
import com.ivyxjc.kotwarden.web.model.SyncResponseModel
import com.ivyxjc.kotwarden.web.notAuthorized
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SyncService(
    private val accountService: AccountService,
    private val cipherService: CipherService,
    private val folderService: FolderService
) {
    fun sync(principal: KotwardenPrincipal, userId: String): SyncResponseModel {
        val resp = SyncResponseModel()
        resp.xyObject = "sync"
        val user = accountService.findById(principal.id) ?: notAuthorized("User not found")
        resp.profile = User.converter.toProfileResponse(user)
        val ciphers = cipherService.findByUser(principal.id)
        val folders = folderService.listByUser(principal.id)
        resp.ciphers.addAll(ciphers.map {
            val r = Cipher.converter.toCipherDetailResponse(it)
            when (r.type) {
                1 -> r.login = decodeFromString(it.data)
                2 -> r.secureNote = decodeFromString(it.data)
                3 -> r.card = decodeFromString(it.data)
                4 -> r.identity = decodeFromString(it.data)
                else -> error("Invalid type")
            }
            r.data = toSome(it)
            if (it.passwordHistory == null) {
                r.passwordHistory = null
            } else {
                r.passwordHistory = decodeFromString(it.passwordHistory!!)
            }
            return@map r
        })
        resp.folders.addAll(folders.map {
            Folder.converter.toFolderResponse(it)
        })
        return resp
    }


}

fun toSome(cipher: Cipher): JsonObject {
    return buildJsonObject {
        put("id", cipher.id)
        put("name", cipher.name)
        put("type", cipher.type)
        put("favorite", cipher.favorite)
        put("createdAt", format(cipher.createdAt))
        put("updatedAt", format(cipher.updatedAt))
        if (cipher.fields != null) {
            put("fields", decodeFromString(cipher.fields)!!)
        }
        decodeFromString<JsonObject>(cipher.data)!!.mapKeys { it.key }.forEach { (k, v) ->
            put(k, v)
        }
    }
}