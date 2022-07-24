package com.ivyxjc.kotwarden

import com.ivyxjc.kotwarden.web.controller.*
import com.ivyxjc.kotwarden.web.service.*
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
    private val log = LoggerFactory.getLogger(ModuleConfig::class.java)
    private val userModule = DI.Module("userModule") {
        bindSingleton { UserRepository(instance()) }
        bindSingleton { AccountService(instance()) }
        bindSingleton { AccountController(instance()) }

        bindSingleton { DeviceRepository(instance()) }
        bindSingleton { DeviceService(instance()) }

        bindSingleton { IdentityService(instance(), instance()) }
        bindSingleton { IdentityController(instance()) }

        bindSingleton { SyncService(instance(), instance(), instance(), instance(), instance()) }
        bindSingleton { SyncController(instance()) }

        bindSingleton { CipherRepository(instance()) }
        bindSingleton { CipherService(instance(), instance(), instance(), instance()) }
        bindSingleton { CipherController(instance()) }

        bindSingleton { FolderRepository(instance()) }
        bindSingleton { FolderService(instance(), instance()) }
        bindSingleton { FolderController(instance()) }


        bindSingleton { UserOrganizationRepository(instance()) }
        bindSingleton { UserOrganizationService(instance()) }

        bindSingleton { UserCollectionRepository(instance()) }

        bindSingleton { VaultCollectionRepository(instance()) }
        bindSingleton { CollectionCipherRepository(instance()) }
        bindSingleton { CollectionService(instance(), instance(), instance()) }

        bindSingleton { OrganizationRepository(instance()) }
        bindSingleton { OrganizationService(instance(), instance(), instance(), instance(), instance()) }
        bindSingleton { OrganizationController(instance(), instance()) }
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