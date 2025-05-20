package com.tommy.siliconflow.app.datasbase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tommy.siliconflow.app.data.network.UserInfo
import com.tommy.siliconflow.app.network.JsonSerializationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val API_KEY_NAME = "api_key"
private const val USER_INFO_NAME = "user_info"

interface SettingDataStore {
    fun getApiKey(): Flow<String?>
    suspend fun saveApiKey(apiKey: String)
    fun getUserInfo(): Flow<UserInfo?>
    suspend fun saveUserInfo(userInfo: UserInfo)
}

class SettingDataStoreImpl(private val dataStore: DataStore<Preferences>) : SettingDataStore {

    private val apiKeyName = stringPreferencesKey(API_KEY_NAME)
    private val userInfoName = stringPreferencesKey(USER_INFO_NAME)

    override fun getApiKey(): Flow<String?> {
        return dataStore.data.map { it[apiKeyName] }
    }

    override suspend fun saveApiKey(apiKey: String) {
        dataStore.edit { it[apiKeyName] = apiKey }
    }

    override fun getUserInfo(): Flow<UserInfo?> {
        return dataStore.data.map {
            it[userInfoName]?.let { userString ->
                JsonSerializationHelper.jsonX().decodeFromString(userString)
            }
        }
    }

    override suspend fun saveUserInfo(userInfo: UserInfo) {
        dataStore.edit {
            it[userInfoName] = JsonSerializationHelper.jsonX().encodeToString(userInfo)
        }
    }
}