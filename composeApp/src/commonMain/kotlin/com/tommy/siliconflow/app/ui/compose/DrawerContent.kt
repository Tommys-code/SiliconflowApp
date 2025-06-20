package com.tommy.siliconflow.app.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.siliconflow.app.data.MainDialog
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.data.network.UserInfo
import com.tommy.siliconflow.app.extensions.getSessionIcon
import com.tommy.siliconflow.app.navigation.AppScreen
import com.tommy.siliconflow.app.ui.components.ImageItem
import com.tommy.siliconflow.app.ui.components.NormalButton
import com.tommy.siliconflow.app.ui.theme.AppColor
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.MainViewEvent
import com.tommy.siliconflow.app.viewmodel.MainViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.cancel
import siliconflowapp.composeapp.generated.resources.create_new_session
import siliconflowapp.composeapp.generated.resources.delete
import siliconflowapp.composeapp.generated.resources.delete_with_num
import siliconflowapp.composeapp.generated.resources.ic_circle_add
import siliconflowapp.composeapp.generated.resources.ic_settings
import siliconflowapp.composeapp.generated.resources.select_all
import siliconflowapp.composeapp.generated.resources.title

@Composable
internal fun DrawerContent(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    postEvent: (MainViewEvent) -> Unit,
) {
    val userInfo = viewModel.userInfo.collectAsState(null).value?.result
    val sessionList = viewModel.sessionList.collectAsStateWithLifecycle(emptyList()).value
    val currentSession = viewModel.currentSession.collectAsStateWithLifecycle().value
    val popupSession = viewModel.mainViewState.popupSession.collectAsStateWithLifecycle(null).value
    val selection = viewModel.mainViewState.selectSessions.collectAsStateWithLifecycle().value
    Column(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f)
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding(),
    ) {
        Text(
            stringResource(Res.string.title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 12.dp),
            color = AppTheme.colorScheme.primaryText,
        )
        HorizontalDivider()
        DrawerCenterList(sessionList, currentSession, popupSession, selection, postEvent)
        HorizontalDivider()
        DrawerBottom(
            userInfo,
            selection != null,
            selection?.size == sessionList.size,
            selection,
            postEvent
        )
    }
}

@Composable
private fun ColumnScope.DrawerCenterList(
    sessionList: List<Session>,
    currentSession: Session?,
    popupSession: Session?,
    selectSessions: List<Session>?,
    doEvent: (MainViewEvent) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(modifier = Modifier.weight(1f)) {
        item {
            Button(
                onClick = {
                    doEvent.invoke(MainViewEvent.ChangeSession(null))
                    doEvent.invoke(MainViewEvent.ToggleDrawer(coroutineScope))
                },
                shape = RoundedCornerShape(size = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colorScheme.backgroundLeve1,
                    contentColor = AppTheme.colorScheme.primaryText,
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 2.dp),
            ) {
                Icon(
                    painterResource(Res.drawable.ic_circle_add),
                    modifier = Modifier.padding(end = 8.dp).size(24.dp),
                    contentDescription = "add",
                )
                Text(
                    stringResource(Res.string.create_new_session),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                )
            }
            HorizontalDivider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
            )
        }
        itemsIndexed(sessionList) { index, data ->
            var itemCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
            val background = if (currentSession == data) {
                AppTheme.colorScheme.container
            } else if (popupSession == data) {
                AppTheme.colorScheme.backgroundLeve1
            } else {
                AppColor.Transparent
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 4.dp)
                    .onGloballyPositioned { itemCoordinates = it }
                    .pointerInput(key1 = data.title, key2 = selectSessions) {
                        detectTapGestures(
                            onLongPress = { offset ->
                                val windowOffset =
                                    itemCoordinates?.localToWindow(offset)?.round()
                                        ?: IntOffset(0, 0)
                                doEvent(MainViewEvent.ShowPopup(data, windowOffset))
                            },
                            onTap = { _ ->
                                if (selectSessions == null) {
                                    doEvent.invoke(MainViewEvent.ChangeSession(data))
                                    doEvent.invoke(MainViewEvent.ToggleDrawer(coroutineScope))
                                } else {
                                    doEvent.invoke(MainViewEvent.SessionCheck(data))
                                }
                            },
                        )
                    }
                    .clip(RoundedCornerShape(size = 10.dp))
                    .background(background)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
            ) {
                Icon(
                    painterResource(data.sessionType.getSessionIcon()),
                    contentDescription = "type",
                    tint = AppTheme.colorScheme.primaryText,
                    modifier = Modifier.padding(end = 6.dp).size(20.dp)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    data.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (currentSession == data) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = AppTheme.colorScheme.primaryText,
                )
                selectSessions?.let {
                    Checkbox(
                        checked = it.contains(data),
                        modifier = Modifier.size(24.dp),
                        colors = AppTheme.checkBoxDefaultColor,
                        onCheckedChange = {
                            doEvent.invoke(MainViewEvent.SessionCheck(data))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerBottom(
    userInfo: UserInfo?,
    mulSelectionMode: Boolean,
    allSelected: Boolean = false,
    selectSessions: List<Session>?,
    doEvent: (MainViewEvent) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (mulSelectionMode) {
            MulSelectionBottom(allSelected, selectSessions, doEvent)
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
                    .clickable {
                        doEvent.invoke(MainViewEvent.Navigate(AppScreen.PersonalInfo))
                    }
            ) {
                ImageItem(
                    userInfo?.image,
                    modifier = Modifier
                        .padding(end = 8.dp).size(36.dp).clip(CircleShape)
                )
                Text(
                    userInfo?.name.orEmpty(),
                    color = AppTheme.colorScheme.primaryText,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(Res.drawable.ic_settings),
                contentDescription = "setting",
                modifier = Modifier.clickable {
                    doEvent.invoke(MainViewEvent.Navigate(AppScreen.SettingGraph))
                },
                tint = AppTheme.colorScheme.primaryText,
            )
        }
    }
}

@Composable
private fun RowScope.MulSelectionBottom(
    allSelected: Boolean,
    selectSessions: List<Session>?,
    doEvent: (MainViewEvent) -> Unit,
) {
    val size = selectSessions?.size ?: 0
    Checkbox(
        checked = allSelected,
        onCheckedChange = {
            doEvent.invoke(MainViewEvent.CheckAll)
        },
        colors = AppTheme.checkBoxDefaultColor,
        modifier = Modifier.size(24.dp),
    )
    Box(
        modifier = Modifier
            .fillMaxHeight().clickable { doEvent.invoke(MainViewEvent.CheckAll) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            stringResource(Res.string.select_all),
            modifier = Modifier.padding(horizontal = 8.dp),
            color = AppTheme.colorScheme.primaryText,
        )
    }
    Spacer(modifier = Modifier.weight(1f))
    NormalButton(
        res = Res.string.cancel,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        contentPadding = PaddingValues(horizontal = 12.dp),
        color = ButtonDefaults.buttonColors(containerColor = AppColor.Transparent),
        onClick = {
            doEvent.invoke(MainViewEvent.MultipleSelectionMode(false))
        },
    )
    Spacer(modifier = Modifier.width(12.dp))
    NormalButton(
        text = if (size > 0)
            stringResource(Res.string.delete_with_num, size)
        else
            stringResource(Res.string.delete),
        contentPadding = PaddingValues(horizontal = 12.dp),
        textColor = AppColor.White,
        color = ButtonDefaults.buttonColors(
            containerColor = AppTheme.colorScheme.highlight,
            disabledContainerColor = AppTheme.colorScheme.highlight.copy(alpha = 0.7f),
        ),
        enable = size > 0,
        onClick = {
            selectSessions?.let {
                doEvent.invoke(MainViewEvent.ShowOrHideDialog(MainDialog.DeleteSessions(it)))
            }
        },
    )
}