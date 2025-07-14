package com.tommy.siliconflow.app.ui.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tommy.siliconflow.app.data.VLMImageData
import com.tommy.siliconflow.app.extensions.isValidUrl
import com.tommy.siliconflow.app.platform.rememberImagerPicker
import com.tommy.siliconflow.app.ui.components.CommonDialog
import com.tommy.siliconflow.app.ui.components.NormalButton
import com.tommy.siliconflow.app.ui.theme.AppColor
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.MainViewEvent
import org.jetbrains.compose.resources.stringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.confirm
import siliconflowapp.composeapp.generated.resources.input_image_url
import siliconflowapp.composeapp.generated.resources.upload_image
import siliconflowapp.composeapp.generated.resources.upload_image_title

@Composable
fun ChooseImageDialog(doEvent: (MainViewEvent) -> Unit) {
    var text by remember { mutableStateOf("") }
    val imagePicker = rememberImagerPicker {
        doEvent.invoke(MainViewEvent.AddImageData(VLMImageData(imageData = it)))
        doEvent.invoke(MainViewEvent.ShowOrHideDialog(null))
    }
    CommonDialog(
        title = stringResource(Res.string.upload_image_title),
        onDismissRequest = { doEvent.invoke(MainViewEvent.ShowOrHideDialog(null)) },
        content = {
            NormalButton(
                res = Res.string.upload_image,
                contentPadding = PaddingValues(horizontal = 12.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                color = ButtonDefaults.buttonColors(containerColor = AppColor.Transparent),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    imagePicker.launchPicker()
                }
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    "or",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = AppTheme.colorScheme.tertiaryText,
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            TextField(
                value = text,
                label = {
                    Text(stringResource(Res.string.input_image_url))
                },
                onValueChange = {
                    text = it
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = AppColor.Transparent,
                    unfocusedIndicatorColor = AppColor.Transparent,
                    focusedContainerColor = AppTheme.colorScheme.backgroundLeve1,
                    unfocusedContainerColor = AppTheme.colorScheme.backgroundLeve1,
                ),
                singleLine = true,
            )
        },
        button = {
            NormalButton(
                res = Res.string.confirm,
                onClick = {
                    if (text.isValidUrl()) {
                        doEvent.invoke(MainViewEvent.AddImageData(VLMImageData(url = text)))
                        doEvent.invoke(MainViewEvent.ShowOrHideDialog(null))
                    }
                },
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier.weight(1f),
                enable = text.isNotBlank()
            )
        }
    )
}