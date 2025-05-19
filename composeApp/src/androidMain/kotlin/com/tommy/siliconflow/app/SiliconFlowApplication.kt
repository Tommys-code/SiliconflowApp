package com.tommy.siliconflow.app

import android.app.Application
import com.tommy.siliconflow.app.di.Factory
import com.tommy.siliconflow.app.di.KMMInject

class SiliconFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KMMInject.initKMM(Factory(this))
    }
}