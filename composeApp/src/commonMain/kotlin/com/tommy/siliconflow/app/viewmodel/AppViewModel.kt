package com.tommy.siliconflow.app.viewmodel

import androidx.lifecycle.ViewModel
import com.tommy.siliconflow.app.datasbase.SettingDataStore

class AppViewModel(settingDataStore: SettingDataStore) : ViewModel() {

    val customAppLocale = settingDataStore.getSettingOptions()

}