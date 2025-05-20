package com.tommy.siliconflow.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tommy.siliconflow.app.datasbase.AppDatabase
import com.tommy.siliconflow.app.datasbase.DB_FILE_NAME
import kotlinx.coroutines.Dispatchers

actual class Factory(private val context: Context) {
    actual fun createSettingDataStore(): DataStore<Preferences> {
        return createDataStore { context.filesDir.resolve(SETTING_STORE_FILE_NAME).absolutePath }
    }

    actual fun createChatDatabase(): AppDatabase {
        val dbFile = context.getDatabasePath(DB_FILE_NAME)
        return Room.databaseBuilder<AppDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath,
        ).setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}