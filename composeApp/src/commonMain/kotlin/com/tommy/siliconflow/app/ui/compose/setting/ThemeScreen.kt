package com.tommy.siliconflow.app.ui.compose.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.siliconflow.app.ui.components.CustomTopBar
import com.tommy.siliconflow.app.ui.theme.CommonColor
import com.tommy.siliconflow.app.viewmodel.SettingViewModel
import org.jetbrains.compose.resources.stringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.dark_theme
import siliconflowapp.composeapp.generated.resources.language_default
import siliconflowapp.composeapp.generated.resources.light_theme
import siliconflowapp.composeapp.generated.resources.setting_language
import siliconflowapp.composeapp.generated.resources.setting_manual
import siliconflowapp.composeapp.generated.resources.theme_default_tips

@Composable
fun ThemeScreen(
    viewModel: SettingViewModel,
    popBack: () -> Unit,
) {
    val settingOptions = viewModel.settingOptions.collectAsStateWithLifecycle(null).value
    Scaffold(
        topBar = {
            CustomTopBar(stringResource(Res.string.setting_language), popBack)
        }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .padding(top = 16.dp, start = 18.dp, end = 18.dp)
        ) {
            settingOptions?.let {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CommonColor.SettingLineBg),
                ) {
                    SystemItem(it.useSystemThem) { checked ->
                        viewModel.changeTheme(checked)
                    }
                }
                if (!it.useSystemThem) {
                    Text(
                        stringResource(Res.string.setting_manual),
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CommonColor.SettingLineBg),
                    ) {
                        LanguageItem(
                            stringResource(Res.string.light_theme),
                            isSelected = !it.isDarkMode
                        ) {
                            viewModel.changeTheme(usSystem = false, isDarkMode = false)
                        }
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        LanguageItem(
                            stringResource(Res.string.dark_theme),
                            isSelected = it.isDarkMode
                        ) {
                            viewModel.changeTheme(usSystem = false, isDarkMode = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemItem(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(Res.string.language_default),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
        Text(
            stringResource(Res.string.theme_default_tips),
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}