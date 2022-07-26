package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.Config
import com.ivyxjc.kotwarden.model.User
import com.ivyxjc.kotwarden.model.UserCollection
import com.ivyxjc.kotwarden.util.USER_PREFIX
import com.ivyxjc.kotwarden.util.convert
import com.ivyxjc.kotwarden.util.hashPassword
import com.ivyxjc.kotwarden.util.verifyPassword
import com.ivyxjc.kotwarden.web.kError
import com.ivyxjc.kotwarden.web.model.KdfRequestModel
import com.ivyxjc.kotwarden.web.model.PreLoginRequest
import com.ivyxjc.kotwarden.web.model.PreLoginResponse
import com.ivyxjc.kotwarden.web.model.RegisterRequest
import io.ktor.http.*
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch
import java.util.*

interface IUserRepository {
    fun findByEmail(email: String): User?
    fun findById(id: String): User?
    fun listByIds(ids: List<String>): List<User>
    fun save(user: User)
}

interface IUserCollectionRepository {
    fun listByUserId(userId: String): List<UserCollection>

}

class UserRepository(private val client: DynamoDbEnhancedClient) : IUserRepository {
    private val schema = TableSchema.fromBean(User::class.java)
    private val table = client.table(User.TABLE_NAME, schema)
    private val emailIndex = table.index(User.Email_INDEX)

    override fun findByEmail(email: String): User? {
        val queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(email).build())
        val iter = emailIndex.query(
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

    override fun listByIds(ids: List<String>): List<User> {
        if (ids.isEmpty()) {
            return listOf()
        }
        val batches = ReadBatch.builder(User::class.java).mappedTableResource(table)
        ids.forEach { batches.addGetItem(Key.builder().partitionValue(it).sortValue(it).build()) }
        val request = BatchGetItemEnhancedRequest.builder().readBatches(batches.build()).build()
        return convert(client.batchGetItem(request), table)
    }

    override fun save(user: User) {
        return table.putItem(user)
    }
}

class UserCollectionRepository(private val client: DynamoDbEnhancedClient) : IUserCollectionRepository {
    private val schema = TableSchema.fromBean(UserCollection::class.java)
    private val table = client.table(UserCollection.TABLE_NAME, schema)
    override fun listByUserId(userId: String): List<UserCollection> {
        val queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build());
        return convert(table.query(queryConditional))
    }

}

class AccountService(private val userRepository: UserRepository) {

    fun preLogin(preLoginRequest: PreLoginRequest): PreLoginResponse {
        val user = userRepository.findByEmail(preLoginRequest.email)
        val (kdfType, kdfIterations) = if (user == null) {
            Pair(Config.config.kdf, Config.config.kdfIterations)
        } else {
            Pair(user.kdf, user.kdfIterations)
        }
        return PreLoginResponse(kdfType, kdfIterations)
    }

    fun register(registerReq: RegisterRequest) {
        val dbUser = userRepository.findByEmail(registerReq.email)
        val user: User
        if (dbUser != null) {
            TODO("throw http exception if user already exists")
        } else {
            // todo: check user invitation
            if (Config.isSignupAllowed(registerReq.email)) {
                user = User.converter.toModel(registerReq)
                user.masterPasswordHash = hashPassword(registerReq.masterPasswordHash, user.salt, user.kdfIterations)
                user.id = USER_PREFIX + UUID.randomUUID().toString()
                user.sk = user.id
                userRepository.save(user)
            } else {
                kError(HttpStatusCode.NotImplemented, "Signup is not allowed for this email")
            }
        }
    }

    fun findById(id: String): User? {
        return userRepository.findById(id)
    }

    /**
     * make sure the userId is from principal
     */
    fun updateKdf(userId: String, request: KdfRequestModel) {
        val user = userRepository.findById(userId) ?: kError("User does not exists")
        if (!verifyPassword(request.masterPasswordHash!!, user.salt, user.masterPasswordHash!!, user.kdfIterations)) {
            kError("Invalid Password")
        } else {
            user.kdf = request.kdf
            user.kdfIterations = request.kdfIterations
            user.masterPasswordHash = hashPassword(request.newMasterPasswordHash, user.salt, request.kdfIterations)
            user.key = request.key
            userRepository.save(user)
        }
    }

    fun userExists(email: String): User? {
        TODO()
    }


}