package com.tommy.siliconflow.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tommy.siliconflow.app.datasbase.AppDatabase
import com.tommy.siliconflow.app.datasbase.DB_FILE_NAME
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithBytes
import platform.Foundation.writeToFile
import platform.UIKit.UIImage
import platform.UIKit.UIImageWriteToSavedPhotosAlbum

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

    actual fun createDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = "${fileDirectory()}/$DB_FILE_NAME"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile,
        )
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

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun saveImage(bytes: ByteArray, fileName: String): Boolean {
        return try {
            val documentsDirectory = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory, NSUserDomainMask, true
            ).first() as String
            val filePath = "$documentsDirectory/$fileName"

            val data = bytes.usePinned { pinned ->
                NSData.dataWithBytes(
                    bytes = pinned.addressOf(0), length = bytes.size.toULong()
                )
            }
            val success = data.writeToFile(filePath, atomically = true)
            if (success) {
                UIImage.imageWithData(data)?.saveToPhotosAlbum()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.saveToPhotosAlbum() {
    UIImageWriteToSavedPhotosAlbum(this, null,null, null)
}
