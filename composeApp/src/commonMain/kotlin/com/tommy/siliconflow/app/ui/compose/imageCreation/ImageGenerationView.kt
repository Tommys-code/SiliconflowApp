package com.tommy.siliconflow.app.ui.compose.imageCreation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.siliconflow.app.data.ImageCreationData
import com.tommy.siliconflow.app.data.Resource
import com.tommy.siliconflow.app.ui.components.ThreeDotLoading
import com.tommy.siliconflow.app.ui.dialog.ImageRatioPopup
import com.tommy.siliconflow.app.ui.dialog.ImageRatioPopupState
import com.tommy.siliconflow.app.ui.dialog.ImageSizePopup
import com.tommy.siliconflow.app.ui.dialog.ImageSizePopupState
import com.tommy.siliconflow.app.ui.theme.AppColor
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.ImageCreationEvent
import com.tommy.siliconflow.app.viewmodel.ImageCreationViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_send
import siliconflowapp.composeapp.generated.resources.image_creation_hilt
import siliconflowapp.composeapp.generated.resources.image_creation_size
import siliconflowapp.composeapp.generated.resources.ratio

@Composable
fun ImageGenerationView(
    sessionID: Long?,
    modifier: Modifier = Modifier,
    viewModel: ImageCreationViewModel = koinViewModel(parameters = { parametersOf(sessionID) })
) {
    val history = viewModel.history.collectAsStateWithLifecycle(emptyList()).value
    val createResult = viewModel.createResult.collectAsStateWithLifecycle().value
    val creationData =
        viewModel.imageCreationData.collectAsStateWithLifecycle(ImageCreationData()).value
    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true,
        ) {
            creationLoadingView(createResult)
            items(history) {
                it.image?.let { img ->
                    ImageCreatedView(
                        img,
                        it.ratio,
                        modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)
                    )
                }
                PromptView(it.prompt)
            }
        }
        ImageCreationView(creationData) { viewModel.doEvent(it) }
    }
}

private fun LazyListScope.creationLoadingView(result: Resource<Unit>) {
    when (result) {
        is Resource.Loading -> {
            if (result.loading) {
                item {
                    ThreeDotLoading(modifier = Modifier.padding(16.dp))
                }
            }
        }

        else -> {}
    }
}

@Composable
private fun PromptView(prompt: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Text(
            prompt,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 12.dp,
                    )
                )
                .background(AppTheme.colorScheme.container)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ImageCreationView(
    data: ImageCreationData,
    doEvent: (ImageCreationEvent) -> Unit
) {
    val popupState = remember { mutableStateOf<ImageRatioPopupState?>(null) }
    val sizePopupState = remember { mutableStateOf<ImageSizePopupState?>(null) }

    var text by remember { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current
    var position by remember { mutableStateOf(IntOffset.Zero) }

    Card(
        modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)
            .onGloballyPositioned { coordinates ->
                position = coordinates.localToWindow(Offset.Zero).round()
            },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            ImageConfigItem(stringResource(Res.string.ratio, data.imageRadio.desc)) {
                popupState.value =
                    ImageRatioPopupState(offset = IntOffset(it.x, position.y), data.imageRadio)
            }
            VerticalDivider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            ImageConfigItem(stringResource(Res.string.image_creation_size, data.batchSize)) {
                sizePopupState.value =
                    ImageSizePopupState(offset = IntOffset(it.x, position.y), data.batchSize)
            }
        }
        HorizontalDivider(thickness = 0.5.dp)
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
                        doEvent.invoke(ImageCreationEvent.Creation(text.text))
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
    ImageRatioPopup(popupState, doEvent)
    ImageSizePopup(sizePopupState, doEvent)
}