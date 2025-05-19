package com.tommy.siliconflow.app.di

import com.tommy.siliconflow.app.datasbase.SettingDataStore
import com.tommy.siliconflow.app.datasbase.SettingDataStoreImpl
import com.tommy.siliconflow.app.network.SiliconFlowClient
import com.tommy.siliconflow.app.network.service.SiliconFlowService
import com.tommy.siliconflow.app.repository.SiliconFlowRepository
import com.tommy.siliconflow.app.viewmodel.MainViewModel
import com.tommy.siliconflow.app.viewmodel.LoginViewModel
import com.tommy.siliconflow.app.viewmodel.SplashViewModel
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

private const val AUTH_CLIENT = "auth_client"
private const val NO_AUTH_CLIENT = "no_auth_client"

object KMMInject {

    fun initKMM(factory: Factory) {
        startKoin {
            modules(
                module { single { factory.createSettingDataStore() } },
                module { single<SettingDataStore> { SettingDataStoreImpl(get()) } },
                module {
                    single<HttpClient>(qualifier(AUTH_CLIENT)) { SiliconFlowClient(get()).buildHttpClient() }
                    single<HttpClient>(qualifier(NO_AUTH_CLIENT)) {
                        SiliconFlowClient(get()).buildHttpClient(
                            false
                        )
                    }
                    single<SiliconFlowService> {
                        SiliconFlowService(
                            get(qualifier(NO_AUTH_CLIENT)),
                            get(qualifier(AUTH_CLIENT))
                        )
                    }
                    single { SiliconFlowRepository(get(), get()) }
                },
                module {
                    viewModel { LoginViewModel(get(), get()) }
                    viewModel { SplashViewModel(get()) }
                    viewModel { MainViewModel(get(), get()) }
                }
            )
        }
    }

}