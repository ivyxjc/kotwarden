//package com.ivyxjc.kotwarden
//
//import com.ivyxjc.kotwarden.util.isEmpty
//import com.ivyxjc.kotwarden.web.model.CipherRequestModel
//import org.junit.Test
//
//
//import io.ktor.http.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import kotlin.test.*
//import io.ktor.server.testing.*
//
//class ApplicationTest {
//    @Test
//    fun testRoot() = testApplication {
//        application {
//        }
//        client.get("/api/health").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            assertEquals("OK", bodyAsText())
//        }
//    }
//}
//
