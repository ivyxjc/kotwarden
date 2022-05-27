package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.model.User
import com.ivyxjc.kotwarden.web.model.PreLoginRequest
import com.ivyxjc.kotwarden.web.model.PreLoginResponse
import com.ivyxjc.kotwarden.web.model.RegisterRequest
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.util.*

class UserRepository(private val client: DynamoDbEnhancedClient) {
    private val schema = TableSchema.fromBean(User::class.java)
    private val table = client.table(User.TABLE_NAME, schema)

    fun findByUser(email: String): User? {
        val key = Key.builder().partitionValue(email).build()
        return table.getItem(key)
    }

    fun save(user: User) {
        return table.putItem(user)
    }
}

interface IAccountService {
    fun register(registerReq: RegisterRequest)
    fun preLogin(preLoginRequest: PreLoginRequest): PreLoginResponse


}

class AccountService(private val userRepository: UserRepository) : IAccountService {

    override fun preLogin(preLoginRequest: PreLoginRequest): PreLoginResponse {
        val user = userRepository.findByUser(preLoginRequest.email)
        val (kdfType, kdfIterations) = if (user == null) {
            Pair(User.KDF_TYPE, User.KDF_ITERATIONS)
        } else {
            Pair(user.kdf, user.kdfIterations)
        }
        return PreLoginResponse(kdfType, kdfIterations)
    }

    override fun register(registerReq: RegisterRequest) {
        val dbUser = userRepository.findByUser(registerReq.email)
        if (dbUser != null) {

        } else {
            val user = User.converter.toModel(registerReq)
            user.id = UUID.randomUUID()
            userRepository.save(user)
        }
    }

    fun userExists(email: String): User? {
        TODO()
    }


}