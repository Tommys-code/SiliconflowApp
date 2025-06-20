package com.tommy.siliconflow.app.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikepenz.markdown.model.State
import com.tommy.siliconflow.app.data.ChatResult
import com.tommy.siliconflow.app.data.MarkdownChatHistory
import com.tommy.siliconflow.app.ui.components.SilMarkDown
import com.tommy.siliconflow.app.ui.dialog.ChatPopup
import com.tommy.siliconflow.app.ui.dialog.ChatPopupState
import com.tommy.siliconflow.app.ui.dialog.ChatType
import com.tommy.siliconflow.app.ui.theme.AppColor
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.MainViewModel
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.enter_question
import siliconflowapp.composeapp.generated.resources.ic_arrow_down
import siliconflowapp.composeapp.generated.resources.ic_send

@Composable
internal fun ChatView(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val popupState = remember { mutableStateOf<ChatPopupState?>(null) }

    val localAnswer = viewModel.answer.conflate().collectAsStateWithLifecycle(null)
    val chatHistory = viewModel.chatHistory.collectAsStateWithLifecycle(emptyList())

    Column(modifier = modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                reverseLayout = true,
                state = listState,
            ) {
                localAnswer.value.let {
                    when (it) {
                        is ChatResult.Progress -> item {
                            it.data.contentMarkdown?.let { content ->
                                ReceiveText(state = content)
                            }
                            it.data.reasoningMarkdown?.let { content ->
                                ThinkingText(state = content)
                            }
                        }

                        is ChatResult.Error -> item { Text(it.e.message.orEmpty()) }
                        else -> {}
                    }
                }
                items(chatHistory.value) { chat ->
                    ChatBox(chat, popupState)
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
                        .clickable { scope.launch { listState.scrollToItem(0) } },
                )
            }
        }
        Card(
            modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Row {
                TextField(
                    value = text,
                    modifier = Modifier.weight(1f),
                    maxLines = 4,
                    label = { Text(stringResource(Res.string.enter_question)) },
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
                    enabled = text.text.isNotBlank() && localAnswer.value !is ChatResult.Progress,
                    onClick = {
                        if (text.text.isNotBlank()) {
                            scope.launch { listState.scrollToItem(0) }
                            focusManager.clearFocus()
                            viewModel.sendData(text.text)
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
    ChatPopup(popupState, viewModel::doEvent)
}

@Composable
private fun ReceiveText(
    state: State,
    modifier: Modifier = Modifier
) {
    SilMarkDown(state = state, modifier = modifier.padding(bottom = 10.dp))
}

@Composable
private fun ThinkingText(
    state: State,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.height(IntrinsicSize.Min).padding(bottom = 12.dp)) {
        VerticalDivider(modifier.fillMaxHeight())
        SilMarkDown(
            state = state,
            modifier = modifier.padding(start = 12.dp),
            paragraphStyle = MaterialTheme.typography.bodyMedium,
            textColor = AppTheme.colorScheme.secondaryText,
        )
    }
}

@Composable
private fun ChatBox(chat: MarkdownChatHistory, popupState: MutableState<ChatPopupState?>) {
    chat.contentMarkdown?.let {
        var receiveCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
        ReceiveText(
            state = it,
            modifier = Modifier
                .onGloballyPositioned { cor -> receiveCoordinates = cor }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { offset ->
                            popupState.value = ChatPopupState(
                                offset = receiveCoordinates?.localToWindow(offset)?.round()
                                    ?: IntOffset(0, 0),
                                history = chat.chatHistory,
                                type = ChatType.RECEIVER,
                            )
                        }
                    )
                }
        )
    }
    chat.thinkingMarkdown?.let {
        var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
        ThinkingText(
            state = it,
            modifier = Modifier
                .onGloballyPositioned { cor -> coordinates = cor }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { offset ->
                            popupState.value = ChatPopupState(
                                offset = coordinates?.localToWindow(offset)?.round()
                                    ?: IntOffset(0, 0),
                                history = chat.chatHistory,
                                type = ChatType.THINKING,
                            )
                        }
                    )
                }
        )
    }
    chat.chatHistory.send?.let {
        var sendCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { cor -> sendCoordinates = cor }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { offset ->
                            popupState.value = ChatPopupState(
                                offset = sendCoordinates?.localToWindow(offset)?.round()
                                    ?: IntOffset(0, 0),
                                history = chat.chatHistory,
                                type = ChatType.SEND,
                            )
                        }
                    )
                },
            contentAlignment = Alignment.CenterEnd,
        ) {
            Text(
                it.content,
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
}
