package com.tommy.siliconflow.app.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.siliconflow.app.data.Resource
import com.tommy.siliconflow.app.extensions.formatPriceSimple
import com.tommy.siliconflow.app.ui.components.CustomTopBar
import com.tommy.siliconflow.app.ui.components.ImageItem
import com.tommy.siliconflow.app.ui.components.LoadingDialog
import com.tommy.siliconflow.app.ui.components.NormalButton
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.UserInfoViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.api_key
import siliconflowapp.composeapp.generated.resources.charge_balance
import siliconflowapp.composeapp.generated.resources.copy_success
import siliconflowapp.composeapp.generated.resources.gift_balance
import siliconflowapp.composeapp.generated.resources.ic_content_copy
import siliconflowapp.composeapp.generated.resources.id
import siliconflowapp.composeapp.generated.resources.logout
import siliconflowapp.composeapp.generated.resources.personal_info_title
import siliconflowapp.composeapp.generated.resources.total_balance

@Composable
internal fun UserInfoScreen(
    popBack: () -> Unit,
    logout: () -> Unit,
    viewModel: UserInfoViewModel = koinViewModel()
) {
    val userInfo = viewModel.userInfo.collectAsStateWithLifecycle().value.result
    val apiKey = viewModel.apiKey.collectAsStateWithLifecycle(null).value
    val showDialog = mutableStateOf(false)

    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current

    val snackBarState = SnackbarHostState()

    scope.launch {
        viewModel.logoutResult.collect { res ->
            when (res) {
                is Resource.Loading -> showDialog.value = res.loading
                is Resource.Success -> {
                    logout()
                    showDialog.value = false
                }

                else -> showDialog.value = false
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarState) },
        topBar = {
            CustomTopBar(
                title = stringResource(Res.string.personal_info_title),
                popBack = popBack,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxWidth().padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            userInfo?.let {
                ImageItem(
                    it.image,
                    modifier = Modifier.size(100.dp)
                        .clip(CircleShape)
                )
                Text(
                    it.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier.padding(top = 12.dp),
                )

                Column(
                    modifier =
                        Modifier.padding(top = 12.dp, start = 24.dp, end = 24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppTheme.colorScheme.backgroundLeve1)
                            .padding(horizontal = 16.dp),
                ) {
                    ColumnItem(stringResource(Res.string.id), it.id) {
                        Icon(
                            painterResource(Res.drawable.ic_content_copy),
                            contentDescription = "copy",
                            modifier = Modifier.padding(start = 12.dp).size(24.dp).clickable {
                                clipboard.setText(AnnotatedString(it.id))
                                scope.launch {
                                    snackBarState.showSnackbar(getString(Res.string.copy_success))
                                }
                            }
                        )
                    }
                    HorizontalDivider(thickness = 0.5.dp)
                    apiKey?.let { key ->
                        ColumnItem(stringResource(Res.string.api_key), key)
                    }
                }

                Column(
                    modifier =
                        Modifier.padding(top = 12.dp, start = 24.dp, end = 24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppTheme.colorScheme.backgroundLeve1)
                            .padding(horizontal = 16.dp),
                ) {
                    ColumnItem(
                        stringResource(Res.string.total_balance),
                        formatPriceSimple(it.totalBalance)
                    )
                    HorizontalDivider(thickness = 0.5.dp)
                    ColumnItem(
                        stringResource(Res.string.gift_balance),
                        formatPriceSimple(it.balance)
                    )
                    HorizontalDivider(thickness = 0.5.dp)
                    ColumnItem(
                        stringResource(Res.string.charge_balance),
                        formatPriceSimple(it.chargeBalance)
                    )
                }
            }
            NormalButton(
                Res.string.logout,
                modifier = Modifier
                    .fillMaxWidth().padding(vertical = 56.dp, horizontal = 24.dp),
                onClick = { viewModel.logout() }
            )
        }
    }
    LoadingDialog(showDialog)
}

@Composable
private fun ColumnItem(
    res: String,
    value: String,
    content: @Composable (() -> Unit)? = null
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Text(res, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.weight(1f))
        Text(value)
        content?.invoke()
    }
}
