package com.tommy.siliconflow.app.di

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tommy.siliconflow.app.datasbase.AppDatabase
import com.tommy.siliconflow.app.datasbase.DB_FILE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    actual suspend fun saveImage(bytes: ByteArray, fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val contentResolver = context.contentResolver
                val uri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                if (uri != null) {
                    contentResolver.openOutputStream(uri).use { outputStream ->
                        if (outputStream != null) {
                            outputStream.write(bytes)

                            contentValues.clear()
                            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                            contentResolver.update(uri, contentValues, null, null)
                            context.sendBroadcast(
                                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)
                            )
                            return@withContext true
                        }
                    }
                }
                return@withContext false
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }
    }
}