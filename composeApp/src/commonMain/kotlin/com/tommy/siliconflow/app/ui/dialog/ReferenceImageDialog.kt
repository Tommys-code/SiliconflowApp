package com.tommy.siliconflow.app.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tommy.siliconflow.app.platform.ImageData
import com.tommy.siliconflow.app.ui.components.ImageItem
import com.tommy.siliconflow.app.viewmodel.ImageCreationEvent
import org.jetbrains.compose.resources.painterResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_close
import siliconflowapp.composeapp.generated.resources.ic_delete

@Composable
fun ReferenceImageDialog(
    imageData: ImageData?,
    show: MutableState<Boolean>,
    doEvent: (ImageCreationEvent) -> Unit,
) {
    if (show.value) {
        imageData?.let {
            ImagePreviewDialog(it.uri, doEvent) {
                show.value = false
            }
        }
    }
}

@Composable
fun ReferenceImageDialog(
    uri: MutableState<String?>,
) {
    uri.value?.let {
        ImagePreviewDialog(it) {
            uri.value = null
        }
    }
}

@Composable
private fun ImagePreviewDialog(
    uri: String,
    doEvent: ((ImageCreationEvent) -> Unit)? = null,
    dismiss: () -> Unit,
) {
    Dialog(onDismissRequest = { dismiss() }) {
        Box {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ImageItem(uri)
                doEvent?.let {
                    IconButton(
                        onClick = {
                            it(ImageCreationEvent.UpdateReferenceImage(null))
                            dismiss()
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_delete),
                            contentDescription = "delete",
                        )
                    }
                }
            }
            Icon(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = "close",
                Modifier
                    .align(Alignment.TopEnd)
                    .offset((18).dp, (-18).dp)
                    .clickable { dismiss() },
            )
        }
    }
}