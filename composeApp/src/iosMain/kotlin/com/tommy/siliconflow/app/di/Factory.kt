package com.tommy.siliconflow.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.tommy.siliconflow.app.datasbase.AppDatabase
import com.tommy.siliconflow.app.datasbase.DB_FILE_NAME
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual class Factory {
    @OptIn(ExperimentalForeignApi::class)
    actual fun createSettingDataStore(): DataStore<Preferences> {
        return createDataStore {
            val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            requireNotNull(documentDirectory).path + "/$SETTING_STORE_FILE_NAME"
        }
    }

    actual fun createChatDatabase(): AppDatabase {
        val dbFile = "${fileDirectory()}/$DB_FILE_NAME"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile,
        ).setDriver(BundledSQLiteDriver())
            .build()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun fileDirectory(): String {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory).path!!
    }
}