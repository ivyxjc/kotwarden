package com.ivyxjc.kotwarden

import com.ivyxjc.kotwarden.web.controller.AccountController
import com.ivyxjc.kotwarden.web.service.AccountService
import com.ivyxjc.kotwarden.web.service.UserRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.slf4j.LoggerFactory
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

object ModuleConfig {
    val log = LoggerFactory.getLogger(ModuleConfig::class.java)
    private val userModule = DI.Module("userModule") {
        bindSingleton { UserRepository(instance()) }
        bindSingleton { AccountService(instance()) }
        bindSingleton { AccountController(instance()) }
    }

    private val dynamodbModule = DI.Module("dynamodb") {
        val t1 = System.currentTimeMillis()
        val client = UrlConnectionHttpClient.builder()
            .build()
        val c = DynamoDbClient.builder().region(Region.US_WEST_2)
            .credentialsProvider(
                EnvironmentVariableCredentialsProvider.create()
            )
            .httpClient(client)
            .build()
        bindSingleton { DynamoDbEnhancedClient.builder().dynamoDbClient(c).build() }
        log.info("dynamodb client costs {}", System.currentTimeMillis() - t1)
    }

    internal val kodein = DI {
        import(userModule)
        import(dynamodbModule)
    }
}