package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.Cipher

class SyncService(private val cipherService: CipherService) {
    fun sync(userId: String): List<Cipher> {
        return cipherService.findByUser(userId)
    }
}