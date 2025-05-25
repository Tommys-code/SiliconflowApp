package com.tommy.siliconflow.app.ui.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tommy.siliconflow.app.data.MainDialog
import com.tommy.siliconflow.app.data.db.ChatHistory
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.ui.components.CommonDialog
import com.tommy.siliconflow.app.ui.components.NormalButton
import com.tommy.siliconflow.app.ui.theme.AppColor
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.MainViewEvent
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.cancel
import siliconflowapp.composeapp.generated.resources.confirm
import siliconflowapp.composeapp.generated.resources.delete_chat_tips
import siliconflowapp.composeapp.generated.resources.delete_dialog_title
import siliconflowapp.composeapp.generated.resources.delete_session_tips
import siliconflowapp.composeapp.generated.resources.edit_session_name
import siliconflowapp.composeapp.generated.resources.edit_session_name_tips
import siliconflowapp.composeapp.generated.resources.finish

@Composable
internal fun MainViewDialog(
    mainDialogState: StateFlow<MainDialog?>,
    doEvent: (MainViewEvent) -> Unit,
) {
    mainDialogState.collectAsState().value?.let {
        when (it) {
            is MainDialog.EditSessionName -> EditSessionNameDialog(it.session, doEvent)
            is MainDialog.DeleteSession -> DeleteSessionDialog(listOf(it.session), doEvent)
            is MainDialog.DeleteSessions -> DeleteSessionDialog(it.sessions, doEvent)
            is MainDialog.DeleteChatHistory -> DeleteChatHistoryDialog(it.history, doEvent)
        }
    }
}

@Composable
private fun EditSessionNameDialog(
    session: Session,
    doEvent: (MainViewEvent) -> Unit,
) {
    val name = remember { mutableStateOf(session.title) }
    CommonDialog(
        title = stringResource(Res.string.edit_session_name),
        content = {
            TextField(
                value = name.value,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,
                textStyle = MaterialTheme.typography.bodyLarge,
                label = { Text(stringResource(Res.string.edit_session_name_tips)) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = AppColor.Transparent,
                    unfocusedIndicatorColor = AppColor.Transparent,
                ),
                onValueChange = {
                    name.value = it
                }
            )
        }
    ) {
        NormalButton(
            modifier = Modifier.weight(1f),
            res = Res.string.cancel,
            onClick = {
                doEvent.invoke(MainViewEvent.ShowOrHideDialog(null))
            },
        )
        NormalButton(
            modifier = Modifier.weight(1f),
            res = Res.string.finish,
            color = ButtonDefaults.buttonColors(
                containerColor = AppTheme.colorScheme.inverseBackground,
            ),
            textColor = AppTheme.colorScheme.inverseText,
            enable = name.value.isNotBlank(),
            onClick = {
                doEvent.invoke(MainViewEvent.EditSessionName(session.copy(title = name.value)))
                doEvent.invoke(MainViewEvent.ShowOrHideDialog(null))
            },
        )
    }
}

@Composable
private fun DeleteSessionDialog(
    session: List<Session>,
    doEvent: (MainViewEvent) -> Unit,
) {
    CommonDialog(
        title = stringResource(Res.string.delete_dialog_title),
        content = {
            Text(
                stringResource(Res.string.delete_session_tips),
                color = AppTheme.colorScheme.primaryText,
                modifier = Modifier.padding(horizontal = 18.dp),
            )
        }
    ) {
        NormalButton(
            modifier = Modifier.weight(1f),
            res = Res.string.cancel,
            onClick = {
                doEvent.invoke(MainViewEvent.ShowOrHideDialog(null))
            },
        )
        NormalButton(
            modifier = Modifier.weight(1f),
            res = Res.string.confirm,
            color = ButtonDefaults.buttonColors(
                containerColor = AppTheme.colorScheme.highlight
            ),
            textColor = AppColor.White,
            onClick = {
                doEvent.invoke(MainViewEvent.DeleteSession(session))
                doEvent.invoke(MainViewEvent.ShowOrHideDialog(null))
            },
        )
    }
}

@Composable
private fun DeleteChatHistoryDialog(
    history: ChatHistory,
    doEvent: (MainViewEvent) -> Unit,
) {
    CommonDialog(
        title = stringResource(Res.string.delete_dialog_title),
        content = {
            Text(
                stringResource(Res.string.delete_chat_tips),
                textAlign = TextAlign.Center,
                color = AppTheme.colorScheme.primaryText,
            )
        }
    ) {
        NormalButton(
            modifier = Modifier.weight(1f),
            res = Res.string.cancel,
            onClick = {
                doEvent.invoke(MainViewEvent.ShowOrHideDialog(null))
            },
        )
        NormalButton(
            modifier = Modifier.weight(1f),
            res = Res.string.confirm,
            color = ButtonDefaults.buttonColors(
                containerColor = AppTheme.colorScheme.highlight,
            ),
            textColor = AppColor.White,
            onClick = {
                doEvent.invoke(MainViewEvent.DeleteChatHistory(history))
                doEvent.invoke(MainViewEvent.ShowOrHideDialog(null))
            },
        )
    }
}
