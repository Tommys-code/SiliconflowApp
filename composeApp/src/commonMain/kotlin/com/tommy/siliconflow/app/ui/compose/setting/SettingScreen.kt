package com.tommy.siliconflow.app.ui.compose.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.siliconflow.app.data.SettingOptions
import com.tommy.siliconflow.app.navigation.AppScreen
import com.tommy.siliconflow.app.ui.components.CustomTopBar
import com.tommy.siliconflow.app.ui.theme.CommonColor
import com.tommy.siliconflow.app.viewmodel.SettingViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_arrow_forward
import siliconflowapp.composeapp.generated.resources.setting
import siliconflowapp.composeapp.generated.resources.setting_language

@Composable
fun SettingScreen(
    navigate: (Any) -> Unit,
    popBack: () -> Unit,
    viewModel: SettingViewModel = koinViewModel(),
) {
    val settingOptions =
        viewModel.settingOptions.collectAsStateWithLifecycle(SettingOptions()).value
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { CustomTopBar(stringResource(Res.string.setting)) { popBack.invoke() } }
    ) { innerPadding ->
        Column(
            modifier =
                Modifier.padding(top = 16.dp, start = 18.dp, end = 18.dp)
                    .padding(innerPadding)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CommonColor.SettingLineBg),
        ) {
            Item(
                stringResource(Res.string.setting_language),
                stringResource(settingOptions.language.title),
            ) {
                navigate(AppScreen.SettingGraph.LanguageSetting)
            }
        }
    }
}

@Composable
private fun Item(title: String, content: String, click: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { click() }
            .padding(vertical = 18.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = content,
            color = Color.Gray,
            modifier = Modifier.padding(end = 8.dp)
        )
        Icon(
            painter = painterResource(Res.drawable.ic_arrow_forward),
            contentDescription = "forward",
            modifier = Modifier.size(12.dp)
        )
    }
}