package com.tommy.siliconflow.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tommy.siliconflow.app.datasbase.AppDatabase
import com.tommy.siliconflow.app.datasbase.DB_FILE_NAME

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Factory(private val context: Context) {
    actual fun createSettingDataStore(): DataStore<Preferences> {
        return createDataStore { context.filesDir.resolve(SETTING_STORE_FILE_NAME).absolutePath }
    }

    actual fun createDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = context.getDatabasePath(DB_FILE_NAME)
        return Room.databaseBuilder<AppDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath,
        )
    }
}