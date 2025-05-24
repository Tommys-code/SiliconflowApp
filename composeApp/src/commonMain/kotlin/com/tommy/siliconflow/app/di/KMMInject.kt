package com.tommy.siliconflow.app.di

import com.tommy.siliconflow.app.datasbase.ChatHistoryStore
import com.tommy.siliconflow.app.datasbase.ChatHistoryStoreImpl
import com.tommy.siliconflow.app.datasbase.ModelStore
import com.tommy.siliconflow.app.datasbase.ModelStoreImpl
import com.tommy.siliconflow.app.datasbase.SettingDataStore
import com.tommy.siliconflow.app.datasbase.SettingDataStoreImpl
import com.tommy.siliconflow.app.datasbase.getRoomDatabase
import com.tommy.siliconflow.app.network.SiliconFlowClient
import com.tommy.siliconflow.app.network.service.SSEService
import com.tommy.siliconflow.app.network.service.SiliconFlowService
import com.tommy.siliconflow.app.repository.ChatRepository
import com.tommy.siliconflow.app.repository.SiliconFlowRepository
import com.tommy.siliconflow.app.viewmodel.AppViewModel
import com.tommy.siliconflow.app.viewmodel.MainViewModel
import com.tommy.siliconflow.app.viewmodel.LoginViewModel
import com.tommy.siliconflow.app.viewmodel.ModelListViewModel
import com.tommy.siliconflow.app.viewmodel.SettingViewModel
import com.tommy.siliconflow.app.viewmodel.SplashViewModel
import com.tommy.siliconflow.app.viewmodel.UserInfoViewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

private const val AUTH_CLIENT = "auth_client"
private const val NO_AUTH_CLIENT = "no_auth_client"
private const val SSE_CLIENT = "sse_client"

object KMMInject {

    private val storeModule = module {
        single<SettingDataStore> { SettingDataStoreImpl(get()) }
        single<ChatHistoryStore> { ChatHistoryStoreImpl(get()) }
        single<ModelStore> { ModelStoreImpl(get(), get()) }
    }

    private val clientModule = module {
        single<HttpClient>(qualifier(AUTH_CLIENT)) { SiliconFlowClient(get()).buildHttpClient() }
        single<HttpClient>(qualifier(NO_AUTH_CLIENT)) {
            SiliconFlowClient(get()).buildHttpClient(false)
        }
        single<HttpClient>(qualifier(SSE_CLIENT)) { SiliconFlowClient(get()).buildSSEClient() }
    }

    private val serviceModule = module {
        single<SiliconFlowService> {
            SiliconFlowService(get(qualifier(NO_AUTH_CLIENT)), get(qualifier(AUTH_CLIENT)))
        }
        single { SSEService(get(qualifier(SSE_CLIENT)), get()) }
        single { SiliconFlowRepository(get(), get()) }
        single { ChatRepository(get(), get(), get(), get()) }
    }

    fun initKMM(factory: Factory) {
        startKoin {
            modules(
                module {
                    single { factory.createSettingDataStore() }
                    single { getRoomDatabase(factory.createDatabaseBuilder()) }
                    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
                },
                storeModule,
                clientModule,
                serviceModule,
                module {
                    viewModelOf(::AppViewModel)
                    viewModelOf(::LoginViewModel)
                    viewModelOf(::SplashViewModel)
                    viewModelOf(::MainViewModel)
                    viewModelOf(::UserInfoViewModel)
                    viewModelOf(::ModelListViewModel)
                    viewModelOf(::SettingViewModel)
                }
            )
        }
    }

}