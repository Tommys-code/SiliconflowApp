package com.tommy.siliconflow.app.datasbase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tommy.siliconflow.app.data.ImageCreationData
import com.tommy.siliconflow.app.data.ImageRatio
import com.tommy.siliconflow.app.data.Language
import com.tommy.siliconflow.app.data.SettingOptions
import com.tommy.siliconflow.app.data.network.UserInfo
import com.tommy.siliconflow.app.network.JsonSerializationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val API_KEY_NAME = "api_key"
private const val USER_INFO_NAME = "user_info"
private const val CHOOSE_MODEL_NAME = "choose_model"

// setting
private const val LANGUAGE_NAME = "language"
private const val USE_SYSTEM_THEME_NAME = "use_system_theme"
private const val IS_DARK_MODE_NAME = "is_dark_mode"

// image
private const val IMAGE_RATIO_NAME = "image_ratio"
private const val IMAGE_BATCH_SIZE_NAME = "image_batch_size"

private const val DEFAULT_MODEL = "Qwen/Qwen3-8B"

interface SettingDataStore {
    fun getApiKey(): Flow<String?>
    suspend fun saveApiKey(apiKey: String)
    fun getUserInfo(): Flow<UserInfo?>
    suspend fun saveUserInfo(userInfo: UserInfo)
    suspend fun removeUserData()
    fun getCurrentModel(): Flow<String?>
    suspend fun chooseModel(model: String)
    fun getSettingOptions(): Flow<SettingOptions>
    suspend fun saveLanguage(language: Language)
    suspend fun changeTheme(useSystem: Boolean = true, isDarkMode: Boolean? = null)

    fun getImageCreationData(): Flow<ImageCreationData>
    suspend fun setImageCreationData(data: ImageCreationData)
}

class SettingDataStoreImpl(private val dataStore: DataStore<Preferences>) : SettingDataStore {

    private val apiKeyName = stringPreferencesKey(API_KEY_NAME)
    private val userInfoName = stringPreferencesKey(USER_INFO_NAME)
    private val aiModelName = stringPreferencesKey(CHOOSE_MODEL_NAME)

    private val languageName = stringPreferencesKey(LANGUAGE_NAME)
    private val useSystemThemeName = booleanPreferencesKey(USE_SYSTEM_THEME_NAME)
    private val isDarkModeName = booleanPreferencesKey(IS_DARK_MODE_NAME)

    private val ratioName = stringPreferencesKey(IMAGE_RATIO_NAME)
    private val sizeName = intPreferencesKey(IMAGE_BATCH_SIZE_NAME)

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

    override fun getSettingOptions(): Flow<SettingOptions> {
        return dataStore.data.map {
            SettingOptions(
                language = Language.valueOf(it[languageName]),
                useSystemThem = it[useSystemThemeName] ?: true,
                isDarkMode = it[isDarkModeName] ?: false,
            )
        }
    }

    override suspend fun saveLanguage(language: Language) {
        dataStore.edit { store ->
            store[languageName] = language.value.orEmpty()
        }
    }

    override suspend fun changeTheme(useSystem: Boolean, isDarkMode: Boolean?) {
        dataStore.edit { store ->
            store[useSystemThemeName] = useSystem
            isDarkMode?.let { store[isDarkModeName] = it }
        }
    }

    override fun getImageCreationData(): Flow<ImageCreationData> {
        return dataStore.data.map {
            ImageCreationData(
                prompt = "",
                imageRadio = ImageRatio.fromValue(it[ratioName]),
                batchSize = it[sizeName] ?: ImageCreationData.DEFAULT_BATCH_SIZE,
            )
        }
    }

    override suspend fun setImageCreationData(data: ImageCreationData) {
        dataStore.edit {
            it[ratioName] = data.imageRadio.value
            it[sizeName] = data.batchSize
        }
    }
}