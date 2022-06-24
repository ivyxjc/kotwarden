package com.ivyxjc.kotwarden.web.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ivyxjc.kotwarden.Config
import com.ivyxjc.kotwarden.model.Device
import com.ivyxjc.kotwarden.model.User
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.time.OffsetDateTime
import java.util.*
import kotlin.random.Random

interface IDeviceRepository {
    fun findByIdAndUser(id: String, userId: String): Device?
    fun findByRefreshToken(refreshToken: String): Device?
    fun deleteAllByUser(userId: String)
    fun save(device: Device)
}

class DeviceRepository(private val client: DynamoDbEnhancedClient) : IDeviceRepository {
    private val schema = TableSchema.fromBean(Device::class.java)
    private val table = client.table(Device.TABLE_NAME, schema)

    override fun findByIdAndUser(id: String, userId: String): Device? {
        val key = Key.builder().partitionValue(id).build()
        // TODO: 2022/6/21 query with userId
        return table.getItem(key)
    }

    override fun findByRefreshToken(refreshToken: String): Device? {
        TODO("Not yet implemented")
    }

    override fun deleteAllByUser(userId: String) {
        TODO("Not yet implemented")
    }

    override fun save(device: Device) {
        return table.putItem(device)
    }


}

class DeviceService(private val deviceRepository: DeviceRepository) {
    fun findByUuidAndUser(uuid: String, userUuid: String): Device? {
        return deviceRepository.findByIdAndUser(userUuid, userUuid)
    }

    fun save(device: Device) {
        return deviceRepository.save(device)
    }

    fun refreshToken(device: Device, user: User, scope: List<String>): Pair<String, Long> {
        if (device.refreshToken.isEmpty()) {
            device.refreshToken = Base64.getEncoder().encodeToString(Random.Default.nextBytes(64))
        }
        device.updatedAt = OffsetDateTime.now()

        //@formatter:off
        return JWT.create()
            .withNotBefore(Date(OffsetDateTime.now().toInstant().toEpochMilli()))
            .withExpiresAt(Date(OffsetDateTime.now().plusHours(Config.defaultValidityHours).toInstant().toEpochMilli()))
            .withIssuer(Config.issuer)
            .withSubject(user.id)
            .withClaim("premium", true)
            .withClaim("name", user.name)
            .withClaim("email", user.email)
            .withClaim("email_verified", true)
            .withClaim("orgowner", mutableListOf<String>())
            .withClaim("orgadmin", mutableListOf<String>())
            .withClaim("orguser", mutableListOf<String>())
            .withClaim("orgmanager", mutableListOf<String>())
            .withClaim("device", device.id)
            .withClaim("scope", scope)
            .withClaim("amr", mutableListOf("Application"))
            .sign(Algorithm.RSA256(Config.publicRsaKey, Config.privateRsaKey)) to Config.defaultValidityHours * 3600L
        //@formatter:on
    }
}