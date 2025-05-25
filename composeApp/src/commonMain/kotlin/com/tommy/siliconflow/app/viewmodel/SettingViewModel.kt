package com.tommy.siliconflow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommy.siliconflow.app.data.Language
import com.tommy.siliconflow.app.datasbase.SettingDataStore
import kotlinx.coroutines.launch

class SettingViewModel(private val settingDataStore: SettingDataStore) : ViewModel() {

    val settingOptions = settingDataStore.getSettingOptions()

    val allLanguages = Language.entries

    fun changeLanguage(language: Language) {
        viewModelScope.launch {
            settingDataStore.saveLanguage(language)
        }
    }

    fun changeTheme(usSystem: Boolean = true, isDarkMode: Boolean? = null) {
        viewModelScope.launch {
            settingDataStore.changeTheme(usSystem, isDarkMode)
        }
    }
}