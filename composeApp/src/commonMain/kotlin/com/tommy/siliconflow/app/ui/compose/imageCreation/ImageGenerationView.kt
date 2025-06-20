package com.tommy.siliconflow.app.ui.compose.imageCreation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.siliconflow.app.data.ImageCreationData
import com.tommy.siliconflow.app.data.ImageRatio
import com.tommy.siliconflow.app.data.Resource
import com.tommy.siliconflow.app.data.db.ImageCreationHistory
import com.tommy.siliconflow.app.extensions.onLongPressClearFocus
import com.tommy.siliconflow.app.platform.ImageData
import com.tommy.siliconflow.app.platform.rememberImagerPicker
import com.tommy.siliconflow.app.ui.components.ImageItem
import com.tommy.siliconflow.app.ui.components.ThreeDotLoading
import com.tommy.siliconflow.app.ui.dialog.ImageBottomDialog
import com.tommy.siliconflow.app.ui.dialog.ImageRatioPopup
import com.tommy.siliconflow.app.ui.dialog.ImageRatioPopupState
import com.tommy.siliconflow.app.ui.dialog.ImageSizePopup
import com.tommy.siliconflow.app.ui.dialog.ImageSizePopupState
import com.tommy.siliconflow.app.ui.dialog.ReferenceImageDialog
import com.tommy.siliconflow.app.ui.theme.AppColor
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.utils.rememberImageProcessing
import com.tommy.siliconflow.app.viewmodel.ImageCreationEvent
import com.tommy.siliconflow.app.viewmodel.ImageCreationViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.choose_reference_image
import siliconflowapp.composeapp.generated.resources.ic_arrow_down
import siliconflowapp.composeapp.generated.resources.ic_arrow_forward
import siliconflowapp.composeapp.generated.resources.ic_send
import siliconflowapp.composeapp.generated.resources.image_creation_hilt
import siliconflowapp.composeapp.generated.resources.image_creation_size
import siliconflowapp.composeapp.generated.resources.ratio
import siliconflowapp.composeapp.generated.resources.reference_image

@Composable
fun ImageGenerationView(
    sessionID: Long?,
    modifier: Modifier = Modifier,
    viewModel: ImageCreationViewModel = koinViewModel(parameters = { parametersOf(sessionID) })
) {
    val history = viewModel.history.collectAsStateWithLifecycle(emptyList()).value
    val createResult = viewModel.createResult.collectAsStateWithLifecycle().value
    val creationData =
        viewModel.imageCreationData.collectAsStateWithLifecycle(null).value

    val listState = rememberLazyListState()
    val bottomDialogState = remember { mutableStateOf<ImageCreationHistory?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                reverseLayout = true,
                state = listState,
            ) {
                creationLoadingView(createResult)
                items(history) {
                    Column(
                        modifier = Modifier.fillParentMaxWidth()
                            .onLongPressClearFocus { bottomDialogState.value = it }
                    ) {
                        PromptView(it.prompt, it.baseImage, it.ratio)
                        it.image?.let { img ->
                            ImageCreatedView(
                                img,
                                it.ratio,
                                modifier = Modifier.padding(bottom = 12.dp),
                                doEvent = { event -> viewModel.doEvent(event) },
                                longPress = { bottomDialogState.value = it }
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            if (listState.canScrollBackward) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_down),
                    contentDescription = "scroll",
                    modifier = Modifier.padding(bottom = 24.dp)
                        .size(36.dp)
                        .align(Alignment.BottomCenter)
                        .shadow(elevation = 5.dp, shape = CircleShape, clip = false)
                        .clip(CircleShape)
                        .background(AppTheme.colorScheme.popContainer)
                        .padding(top = 4.dp)
                        .clickable { viewModel.doEvent(ImageCreationEvent.ScrollTOBottom) },
                )
            }
        }
        creationData?.let { data -> ImageCreationView(data) { viewModel.doEvent(it) } }
    }
    ImageBottomDialog(bottomDialogState) { viewModel.doEvent(it) }

    LaunchedEffect(Unit) {
        viewModel.viewEvent.collectLatest {
            if (it == ImageCreationEvent.ScrollTOBottom) {
                listState.scrollToItem(0)
            }
        }
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
private fun PromptView(prompt: String, fileName: String?, ratio: String) {
    val referenceImagePreview = remember { mutableStateOf<String?>(null) }
    val imageProcessing = rememberImageProcessing()
    Column(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        Text(
            buildAnnotatedString {
                append(prompt)
                withStyle(SpanStyle(fontSize = MaterialTheme.typography.labelSmall.fontSize)) {
                    append(" - ")
                    append(ImageRatio.fromValue(ratio).desc)
                }
            },
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomEnd = 12.dp,
                    )
                )
                .background(AppTheme.colorScheme.container)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
        fileName?.let { imageProcessing.getReferenceImageUri(it) }?.takeIf { it.isNotBlank() }
            ?.let {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp)
                        .clickable { referenceImagePreview.value = it },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(Res.string.reference_image),
                        style = MaterialTheme.typography.labelSmall
                    )
                    ImageItem(it, modifier = Modifier.size(16.dp))
                }
            }
    }
    ReferenceImageDialog(referenceImagePreview)
}

@Composable
private fun ImageCreationView(
    data: ImageCreationData,
    doEvent: (ImageCreationEvent) -> Unit
) {
    val popupState = remember { mutableStateOf<ImageRatioPopupState?>(null) }
    val sizePopupState = remember { mutableStateOf<ImageSizePopupState?>(null) }

    val baseInfo = data.baseInfo
    val dynamicData = data.dynamicData

    var text by remember { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current
    var position by remember { mutableStateOf(IntOffset.Zero) }

    val showReferenceImagePreview = remember { mutableStateOf(false) }
    val imagePicker = rememberImagerPicker { doEvent(ImageCreationEvent.UpdateReferenceImage(it)) }
    val imageProcessing = rememberImageProcessing()
    val scope = rememberCoroutineScope()

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
            ImageConfigItem(stringResource(Res.string.ratio, baseInfo.imageRadio.desc)) {
                popupState.value =
                    ImageRatioPopupState(offset = IntOffset(it.x, position.y), baseInfo.imageRadio)
            }
            VerticalDivider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            ImageConfigItem(stringResource(Res.string.image_creation_size, baseInfo.batchSize)) {
                sizePopupState.value =
                    ImageSizePopupState(offset = IntOffset(it.x, position.y), baseInfo.batchSize)
            }
            VerticalDivider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            ReferenceImageItem(dynamicData.referenceImage) {
                if (dynamicData.referenceImage != null) {
                    showReferenceImagePreview.value = true
                } else {
                    imagePicker.launchPicker()
                }
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
                        scope.launch {
                            doEvent.invoke(ImageCreationEvent.ScrollTOBottom)
                            focusManager.clearFocus()
                            val referenceImageInfo = data.dynamicData.referenceImage?.uri?.let {
                                imageProcessing.getReferenceImageInfoFromUri(it)
                            }
                            doEvent.invoke(
                                ImageCreationEvent.Creation(text.text, referenceImageInfo)
                            )
                            text = TextFieldValue("")
                        }
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
    ReferenceImageDialog(dynamicData.referenceImage, showReferenceImagePreview, doEvent)
}

@Composable
private fun ReferenceImageItem(
    imageData: ImageData?,
    onClick: () -> Unit,
) {
    val text = imageData?.let { stringResource(Res.string.reference_image) }
        ?: stringResource(Res.string.choose_reference_image)
    Row(
        modifier = Modifier
            .border(
                BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text,
            modifier = Modifier
                .padding(vertical = 8.dp),
            color = AppTheme.colorScheme.primaryText,
            style = MaterialTheme.typography.bodyMedium,
        )
        imageData?.let {
            ImageItem(it.uri, modifier = Modifier.size(24.dp))
        } ?: run {
            Icon(
                modifier = Modifier.size(12.dp).padding(start = 2.dp),
                painter = painterResource(Res.drawable.ic_arrow_forward),
                contentDescription = "choose",
                tint = AppTheme.colorScheme.primaryText,
            )
        }
    }
}