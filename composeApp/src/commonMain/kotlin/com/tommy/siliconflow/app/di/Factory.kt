package com.tommy.siliconflow.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import com.tommy.siliconflow.app.datasbase.AppDatabase
import okio.Path.Companion.toPath

internal const val SETTING_STORE_FILE_NAME = "setting.preferences_pb"

expect class Factory {
    fun createSettingDataStore(): DataStore<Preferences>
    fun createDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
    suspend fun saveImage(bytes: ByteArray, fileName: String): Boolean
}

internal fun createDataStore(
    producePath: () -> String,
): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
    corruptionHandler = null,
    migrations = emptyList(),
    produceFile = { producePath().toPath() },
)