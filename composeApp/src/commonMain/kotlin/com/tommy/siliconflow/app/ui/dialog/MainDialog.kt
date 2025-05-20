package com.tommy.siliconflow.app.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tommy.siliconflow.app.data.MainDialog
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.ui.components.NormalButton
import com.tommy.siliconflow.app.viewmodel.MainViewEvent
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.cancel
import siliconflowapp.composeapp.generated.resources.edit_session_name
import siliconflowapp.composeapp.generated.resources.edit_session_name_tips
import siliconflowapp.composeapp.generated.resources.finish

@Composable
internal fun MainViewDialog(
    mainDialogState: StateFlow<MainDialog?>,
    doEvent: (MainViewEvent) -> Unit,
) {
    mainDialogState.collectAsState().value?.let {
        Dialog(onDismissRequest = { }) {
            when (it) {
                is MainDialog.EditSessionName -> EditSessionNameDialog(it.session, doEvent)
                is MainDialog.DeleteSession -> DeleteSessionDialog()
            }
        }
    }
}

@Composable
private fun EditSessionNameDialog(
    session: Session,
    doEvent: (MainViewEvent) -> Unit,
) {
    val name = remember { mutableStateOf(session.title) }
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(Res.string.edit_session_name),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(vertical = 12.dp)
        )
        TextField(
            value = name.value,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            maxLines = 1,
            textStyle = MaterialTheme.typography.bodyLarge,
            label = { Text(stringResource(Res.string.edit_session_name_tips)) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            onValueChange = {
                name.value = it
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                    containerColor = Color.Black
                ),
                textColor = Color.White,
                enable = name.value.isNotBlank(),
                onClick = {
                    doEvent.invoke(MainViewEvent.EditSessionName(session.copy(title = name.value)))
                    doEvent.invoke(MainViewEvent.ShowOrHideDialog(null))
                },
            )
        }
    }
}

@Composable
private fun DeleteSessionDialog() {

}
