package com.tommy.siliconflow.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual class Factory(private val context: Context) {
    actual fun createSettingDataStore(): DataStore<Preferences> {
        return createDataStore { context.filesDir.resolve(SETTING_STORE_FILE_NAME).absolutePath }
    }
}