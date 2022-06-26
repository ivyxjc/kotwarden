package com.ivyxjc.kotwarden.web.model

import com.ivyxjc.kotwarden.model.serializer.OffsetDatetimeSerializer
import java.time.OffsetDateTime


@kotlinx.serialization.Serializable
data class CipherRequestModel(
    val name: String,
    val type: Int,
    val organizationId: String? = null,
    val folderId: String? = null,
    val favorite: Boolean? = null,
    val reprompt: Int? = null,
    val notes: String? = null,
    val fields: Array<CipherFieldModel>? = null,
    val passwordHistory: Array<CipherPasswordHistoryModel>? = null,
    val attachments: Map<String, String>? = null,
    val attachments2: Map<String, CipherAttachmentModel>? = null,
    val login: CipherLoginModel? = null,
    val card: CipherCardModel? = null,
    val identity: CipherIdentityModel? = null,
    val secureNote: CipherSecureNoteModel? = null,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeSerializer::class)
    val lastKnownRevisionDate: OffsetDateTime? = null
)

@kotlinx.serialization.Serializable
data class CipherFieldModel(
    val type: Int? = null,
    val name: String? = null,
    val value: String? = null
)

@kotlinx.serialization.Serializable
data class CipherPasswordHistoryModel(
    val password: String,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeSerializer::class)
    val lastUsedDate: OffsetDateTime?
)

@kotlinx.serialization.Serializable
data class CipherAttachmentModel(
    val fileName: String? = null,
    val key: String? = null
)

@kotlinx.serialization.Serializable
data class CipherLoginModel(
    val uri: String? = null,
    val uris: Array<CipherLoginUriModel>? = null,
    val username: String? = null,
    val password: String? = null,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeSerializer::class)
    val passwordRevisionDate: OffsetDateTime? = null,
    val totp: String? = null,
    val autofillOnPageLoad: Boolean? = null
)

@kotlinx.serialization.Serializable
data class CipherCardModel(
    val cardholderName: String? = null,
    val brand: String? = null,
    val number: String? = null,
    val expMonth: String? = null,
    val expYear: String? = null,
    val code: String? = null
)

@kotlinx.serialization.Serializable
data class CipherIdentityModel(
    val title: String? = null,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val address1: String? = null,
    val address2: String? = null,
    val address3: String? = null,
    val city: String? = null,
    val state: String? = null,
    val postalCode: String? = null,
    val country: String? = null,
    val company: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val ssn: String? = null,
    val username: String? = null,
    val passportNumber: String? = null,
    val licenseNumber: String? = null
)

@kotlinx.serialization.Serializable
data class CipherSecureNoteModel(
    val type: Int? = null
)

@kotlinx.serialization.Serializable
data class CipherLoginUriModel(
    val uri: String? = null,
    val match: Int? = null
)