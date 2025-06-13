package com.tommy.siliconflow.app.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog
import com.tommy.siliconflow.app.platform.ImageData
import com.tommy.siliconflow.app.ui.components.ImageItem
import com.tommy.siliconflow.app.viewmodel.ImageCreationEvent
import org.jetbrains.compose.resources.painterResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_delete

@Composable
fun ReferenceImageDialog(
    imageData: ImageData?,
    show: MutableState<Boolean>,
    doEvent: (ImageCreationEvent) -> Unit,
) {
    if (show.value) {
        imageData?.let {
            Dialog(onDismissRequest = { show.value = false }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ImageItem(it.uri)
                    IconButton(
                        onClick = {
                            doEvent(ImageCreationEvent.UpdateReferenceImage(null))
                            show.value = false
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_delete),
                            contentDescription = "delete",
                        )
                    }
                }
            }
        }
    }
}