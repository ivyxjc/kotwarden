package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.Cipher
import com.ivyxjc.kotwarden.model.User
import com.ivyxjc.kotwarden.util.EMPTY_STRING
import com.ivyxjc.kotwarden.util.format
import com.ivyxjc.kotwarden.web.model.KotwardenPrincipal
import com.ivyxjc.kotwarden.web.model.SyncResponseModel
import com.ivyxjc.kotwarden.web.notAuthorized
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SyncService(private val accountService: AccountService, private val cipherService: CipherService) {
    fun sync(principal: KotwardenPrincipal, userId: String): SyncResponseModel {
        val resp = SyncResponseModel()
        resp.xyObject = "sync"
        val user = accountService.findById(principal.id) ?: notAuthorized("User not found")
        resp.profile = User.converter.toProfileResponse(user)
        val ciphers = cipherService.findByUser(principal.id)
        resp.ciphers.addAll(ciphers.map {
            val r = Cipher.converter.toCipherDetailResponse(it)
            when (r.type) {
                1 -> r.login = Json.decodeFromString(it.data)
                2 -> r.secureNote = Json.decodeFromString(it.data)
                3 -> r.card = Json.decodeFromString(it.data)
                4 -> r.identity = Json.decodeFromString(it.data)
                else -> error("Invalid type")
            }
            r.data = toSome(it)
            return@map r
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
        put("fields", Json.decodeFromString(cipher.fields ?: EMPTY_STRING))
        Json.decodeFromString<JsonObject>(cipher.data).mapKeys { it.key }.forEach { (k, v) ->
            put(k, v)
        }
    }
}