package com.tommy.siliconflow.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.tommy.siliconflow.app.datasbase.AppDatabase
import okio.Path.Companion.toPath

internal const val SETTING_STORE_FILE_NAME = "setting.preferences_pb"

expect class Factory {
    fun createSettingDataStore(): DataStore<Preferences>
    fun createChatDatabase(): AppDatabase
}

internal fun createDataStore(
    producePath: () -> String,
): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
    corruptionHandler = null,
    migrations = emptyList(),
    produceFile = { producePath().toPath() },
)