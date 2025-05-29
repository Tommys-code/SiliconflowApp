package com.tommy.siliconflow.app.ui.compose.imageCreation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.tommy.siliconflow.app.ui.theme.AppColor
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.ImageCreationViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_send
import siliconflowapp.composeapp.generated.resources.image_creation_hilt

@Composable
fun ImageGenerationView(
    sessionID: Long?,
    modifier: Modifier = Modifier,
    viewModel: ImageCreationViewModel = koinViewModel(parameters = { parametersOf(sessionID) })
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {

        }
        ImageCreationView()
    }
}

@Composable
private fun ImageCreationView() {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current

    Card(
        modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row {
            TextField(
                value = text,
                modifier = Modifier.weight(1f),
                maxLines = 4,
                label = { Text(stringResource(Res.string.image_creation_hilt)) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = AppColor.Transparent,
                    unfocusedIndicatorColor = AppColor.Transparent,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                onValueChange = { text = it },
                keyboardActions = KeyboardActions(onNext = {
                    text = text.copy(
                        text = text.text + "\n",
                        selection = TextRange(text.text.length + 1)
                    )
                })
            )
            IconButton(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(end = 8.dp, bottom = 8.dp)
                    .clip(CircleShape)
                    .size(30.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = AppTheme.colorScheme.iconContainer,
                    disabledContainerColor = AppTheme.colorScheme.iconContainerDisable,
                    contentColor = AppTheme.colorScheme.icon,
                ),
                enabled = text.text.isNotBlank(),
                onClick = {
                    if (text.text.isNotBlank()) {
                        focusManager.clearFocus()
//                        viewModel.sendData(text.text)
                        text = TextFieldValue("")
                    }
                },
            ) {
                Icon(
                    modifier = Modifier.size(16.dp).rotate(270f),
                    painter = painterResource(Res.drawable.ic_send),
                    contentDescription = "send",
                )
            }
        }
    }
}