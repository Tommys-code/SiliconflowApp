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
private const val CHOOSE_MODEL_NAME = "choose_model"

private const val DEFAULT_MODEL = "Qwen/Qwen3-8B"

interface SettingDataStore {
    fun getApiKey(): Flow<String?>
    suspend fun saveApiKey(apiKey: String)
    fun getUserInfo(): Flow<UserInfo?>
    suspend fun saveUserInfo(userInfo: UserInfo)
    suspend fun removeUserData()
    fun getCurrentModel(): Flow<String?>
    suspend fun chooseModel(model: String)
}

class SettingDataStoreImpl(private val dataStore: DataStore<Preferences>) : SettingDataStore {

    private val apiKeyName = stringPreferencesKey(API_KEY_NAME)
    private val userInfoName = stringPreferencesKey(USER_INFO_NAME)
    private val aiModelName = stringPreferencesKey(CHOOSE_MODEL_NAME)

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

    override suspend fun removeUserData() {
        dataStore.edit {
            it.remove(apiKeyName)
            it.remove(userInfoName)
        }
    }

    override fun getCurrentModel(): Flow<String> {
        return dataStore.data.map { it[aiModelName] ?: DEFAULT_MODEL }
    }

    override suspend fun chooseModel(model: String) {
        dataStore.edit { it[aiModelName] = model }
    }
}