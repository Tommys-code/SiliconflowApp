package com.tommy.siliconflow.app.ui.compose.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.siliconflow.app.data.Language
import com.tommy.siliconflow.app.ui.components.CustomTopBar
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.SettingViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_check
import siliconflowapp.composeapp.generated.resources.language_default_tips
import siliconflowapp.composeapp.generated.resources.setting_language

@Composable
fun LanguageSettingScreen(
    viewModel: SettingViewModel,
    popBack: () -> Unit,
) {
    val settingOptions = viewModel.settingOptions.collectAsStateWithLifecycle(null).value
    Scaffold(
        topBar = {
            CustomTopBar(stringResource(Res.string.setting_language), popBack)
        }) { innerPadding ->
        Column(
            modifier = Modifier.padding(top = 16.dp, start = 18.dp, end = 18.dp)
                .padding(innerPadding).clip(RoundedCornerShape(12.dp))
                .background(AppTheme.colorScheme.backgroundLeve1),
        ) {
            viewModel.allLanguages.forEachIndexed { index, it ->
                LanguageItem(
                    title = stringResource(it.title),
                    desc = if (it == Language.DEFAULT) stringResource(Res.string.language_default_tips) else null,
                    isSelected = it == settingOptions?.language,
                    click = {
                        if (it == settingOptions?.language) return@LanguageItem
                        viewModel.changeLanguage(it)
                    },
                )
                if (index != viewModel.allLanguages.lastIndex) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
internal fun LanguageItem(
    title: String,
    desc: String? = null,
    isSelected: Boolean,
    click: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { click() }
            .padding(vertical = 18.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            desc?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.secondaryText,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        if (isSelected) {
            Icon(
                painter = painterResource(Res.drawable.ic_check),
                contentDescription = "check",
                modifier = Modifier.padding(start = 8.dp).size(24.dp)
            )
        }
    }
}