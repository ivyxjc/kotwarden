package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.Config
import com.ivyxjc.kotwarden.model.User
import com.ivyxjc.kotwarden.util.USER_ID_PREFIX
import com.ivyxjc.kotwarden.util.convert
import com.ivyxjc.kotwarden.util.hashPassword
import com.ivyxjc.kotwarden.web.model.PreLoginRequest
import com.ivyxjc.kotwarden.web.model.PreLoginResponse
import com.ivyxjc.kotwarden.web.model.RegisterRequest
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import java.util.*

interface IUserRepository {
    fun findByEmail(email: String): User?
    fun findById(id: String): User?
    fun save(user: User)
}

class UserRepository(private val client: DynamoDbEnhancedClient) : IUserRepository {
    private val schema = TableSchema.fromBean(User::class.java)
    private val table = client.table(User.TABLE_NAME, schema)

    override fun findByEmail(email: String): User? {
        val queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(email).build())
        val idx = table.index(User.Email_INDEX)
        val iter = idx.query(
            QueryEnhancedRequest.builder().queryConditional(queryConditional).build()
        )
        val list = convert(iter)
        return if (list.isNotEmpty()) {
            list[0]
        } else {
            null
        }
    }

    override fun findById(id: String): User? {
        val key = Key.builder().partitionValue(id).sortValue(id).build()
        return table.getItem(key)
    }

    override fun save(user: User) {
        user.id = USER_ID_PREFIX + user.id
        user.sk = user.id
        return table.putItem(user)
    }
}

interface IAccountService {
    fun register(registerReq: RegisterRequest)
    fun preLogin(preLoginRequest: PreLoginRequest): PreLoginResponse

    fun findById(id: String): User?
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
                user.masterPasswordHash = hashPassword(registerReq.masterPasswordHash, user.salt, user.kdfIterations)
                user.id = UUID.randomUUID().toString()
                user.sk = user.id
                userRepository.save(user)
            } else {
                TODO("throw http exception if user email is not allowed to sign up")
            }
        }
    }

    override fun findById(id: String): User? {
        return userRepository.findById(id)
    }

    fun userExists(email: String): User? {
        TODO()
    }


}