package com.ivyxjc.kotwarden.web.controller

import com.ivyxjc.kotwarden.model.Organization
import com.ivyxjc.kotwarden.web.kotwardenPrincipal
import com.ivyxjc.kotwarden.web.model.OrganizationCreateRequestModel
import com.ivyxjc.kotwarden.web.model.PlanResponseModel
import com.ivyxjc.kotwarden.web.service.OrganizationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.json.*


class OrganizationController(private val organizationService: OrganizationService) {
    suspend fun getPlans(ctx: ApplicationCall) {
        ctx.apply {
            val json = buildJsonObject {
                put("object", "list")
                put("continuationToken", null as String?)
                putJsonArray("data") {
                    add(buildJsonObject {
                        put("object", "plan")
                        put("type", 0)
                        put("product", 0)
                        put("name", "Free")
                        put("nameLocalizationKey", "planNameFree")
                        put("descriptionLocalizationKey", "planDescFree")
                    })
                }

            }
            this.respond(HttpStatusCode.OK, json)
        }
    }

    suspend fun listOrganizations(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val list = organizationService.listByUserId(principal.id)
                .map { Organization.converter.toProfileResponse(it.second) }
            this.respond(HttpStatusCode.OK, list)
        }
    }

    suspend fun createOrganization(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<OrganizationCreateRequestModel>()
            val organization = organizationService.createOrganization(principal, request)
            val json = buildJsonObject {
                put("object", "organization")
                put("id", organization.id)
                put("name", organization.name)
                put("billingEmail", organization.billingEmail)
                put("identifier", null as String?)
                put("businessName", null as String?)
                put("businessAddress1", null as String?)
                put("businessAddress2", null as String?)
                put("businessAddress3", null as String?)
                put("businessCountry", null as String?)
                put("businessTaxNumber", null as String?)
                put("planType", 0)
                put("seats", 100)
                put("maxAutoscaleSeats", null as String?)
                put("maxCollections", 2)
                put("maxStorageGb", null as String?)
                put("usePolicies", false)
                put("useSso", false)
                put("useKeyConnector", false)
                put("useGroups", false)
                put("useDirectory", false)
                put("useEvents", false)
                put("useTotp", false)
                put("use2fa", false)
                put("useApi", false)
                put("useResetPassword", false)
                put("usersGetPremium", false)
                put("selfHost", false)
                put("hasPublicAndPrivateKeys", true)
                put(
                    "plan", Json.encodeToJsonElement(
                        PlanResponseModel(
                            `object` = "plan",
                            type = 0,
                            product = 0,
                            name = "Free", nameLocalizationKey = "planNameFree",
                            descriptionLocalizationKey = "planDescFree",
                            baseSeats = 100,
                            maxCollections = 100,
                            maxUsers = 100,
                        )
                    )

                )
            }
            this.respond(HttpStatusCode.OK, json)
        }

    }

}