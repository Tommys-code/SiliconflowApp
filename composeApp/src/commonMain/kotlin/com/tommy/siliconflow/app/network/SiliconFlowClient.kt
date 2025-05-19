package com.tommy.siliconflow.app.network

import com.tommy.siliconflow.app.datasbase.SettingDataStore
import com.tommy.siliconflow.app.network.error.AuthException
import com.tommy.siliconflow.app.network.error.GeneralException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.first

private const val BASE_URL = "https://api.siliconflow.cn/v1/"

class SiliconFlowClient(private val dataStore: SettingDataStore) {

    fun buildHttpClient(auth: Boolean = true): HttpClient {
        return HttpClient {
            defaultRequest {
                contentType(ContentType.Application.Json)
                url(BASE_URL)
            }
            install(ContentNegotiation) {
                json(json = JsonSerializationHelper.jsonX())
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }
            if (auth) {
                install(Auth) {
                    bearer {
                        loadTokens {
                            BearerTokens(getApiKey(), null)
                        }
                    }
                }
            }
            expectSuccess = true // trigger response validator, see HttpResponseValidator
            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, _ ->
                    (cause as? ResponseException)?.let {
                        generateExceptionFromErrorBody(it)
                    }?.let { e ->
                        throw e
                    }
                }
            }
        }
    }

    private suspend fun getApiKey(): String {
        return dataStore.getApiKey().conflate().first().orEmpty()
    }

    private suspend fun generateExceptionFromErrorBody(e: ResponseException): Exception {
        if (e.response.status == HttpStatusCode.Unauthorized) {
            return AuthException(e.response.body())
        } else {
            return GeneralException(e.response.body())
        }
    }
}