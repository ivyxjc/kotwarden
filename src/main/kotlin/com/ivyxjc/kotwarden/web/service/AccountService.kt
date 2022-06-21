package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.Config
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

    fun findByEmail(email: String): User? {
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
        val user = userRepository.findByEmail(preLoginRequest.email)
        val (kdfType, kdfIterations) = if (user == null) {
            Pair(Config.kdf, Config.kdfIterations)
        } else {
            Pair(user.kdf, user.kdfIterations)
        }
        return PreLoginResponse(kdfType, kdfIterations)
    }

    override fun register(registerReq: RegisterRequest) {
        val dbUser = userRepository.findByEmail(registerReq.email)
        val user: User
        if (dbUser != null) {
            TODO("throw http exception if user already exists")
        } else {
            // todo: check user invitation
            if (Config.isSignupAllowed(registerReq.email)) {
                user = User.converter.toModel(registerReq)
                user.id = UUID.randomUUID()
                userRepository.save(user)
            } else {
                TODO("throw http exception if user email is not allowed to sign up")
            }
        }
    }

    fun userExists(email: String): User? {
        TODO()
    }


}