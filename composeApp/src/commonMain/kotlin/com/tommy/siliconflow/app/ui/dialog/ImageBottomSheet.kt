package com.tommy.siliconflow.app.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tommy.siliconflow.app.data.db.ImageCreationHistory
import com.tommy.siliconflow.app.ui.components.CommonDialog
import com.tommy.siliconflow.app.ui.components.NormalButton
import com.tommy.siliconflow.app.ui.theme.AppColor
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.ImageCreationEvent
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.cancel
import siliconflowapp.composeapp.generated.resources.confirm
import siliconflowapp.composeapp.generated.resources.copy_prompt
import siliconflowapp.composeapp.generated.resources.copy_success
import siliconflowapp.composeapp.generated.resources.delete
import siliconflowapp.composeapp.generated.resources.delete_chat_tips
import siliconflowapp.composeapp.generated.resources.delete_dialog_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageBottomDialog(
    state: MutableState<ImageCreationHistory?>,
    doEvent: (ImageCreationEvent) -> Unit,
) {
    val showDeleteDialog = remember { mutableStateOf<ImageCreationHistory?>(null) }
    state.value?.let {
        val sheetState = rememberModalBottomSheetState()
        val clipboard = LocalClipboardManager.current
        val scope = rememberCoroutineScope()

        fun hide() {
            scope.launch {
                sheetState.hide()
                state.value = null
            }
        }

        ModalBottomSheet(
            onDismissRequest = { state.value = null },
            containerColor = AppColor.Transparent,
            dragHandle = null,
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.colorScheme.popContainer),
            ) {
                Text(
                    stringResource(Res.string.copy_prompt),
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            clipboard.setText(AnnotatedString(it.prompt))
                            doEvent.invoke(ImageCreationEvent.ShowToast(Res.string.copy_success))
                            hide()
                        }
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                )
                HorizontalDivider(thickness = 0.5.dp)
                Text(
                    stringResource(Res.string.delete),
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            showDeleteDialog.value = it
                            hide()
                        }
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    color = AppTheme.colorScheme.highlight,
                )
            }

            Text(
                stringResource(Res.string.cancel),
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { hide() }
                    .background(AppTheme.colorScheme.popContainer)
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
    showDeleteDialog.value?.let {
        DeleteImageCreationHistoryDialog(it, doEvent) { showDeleteDialog.value = null }
    }
}

@Composable
private fun DeleteImageCreationHistoryDialog(
    history: ImageCreationHistory,
    doEvent: (ImageCreationEvent) -> Unit,
    dismiss: () -> Unit,
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
            onClick = dismiss,
        )
        NormalButton(
            modifier = Modifier.weight(1f),
            res = Res.string.confirm,
            color = ButtonDefaults.buttonColors(
                containerColor = AppTheme.colorScheme.highlight,
            ),
            textColor = AppColor.White,
            onClick = {
                doEvent.invoke(ImageCreationEvent.DeleteHistory(history))
                dismiss()
            },
        )
    }
}