package com.tommy.siliconflow.app.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
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
import com.tommy.siliconflow.app.data.ChatResult
import com.tommy.siliconflow.app.data.db.ChatHistory
import com.tommy.siliconflow.app.ui.dialog.ChatPopup
import com.tommy.siliconflow.app.ui.dialog.ChatPopupState
import com.tommy.siliconflow.app.ui.dialog.ChatType
import com.tommy.siliconflow.app.ui.theme.CommonColor
import com.tommy.siliconflow.app.viewmodel.MainViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.enter_question
import siliconflowapp.composeapp.generated.resources.ic_send

@Composable
internal fun ChatView(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current
    val popupState = remember { mutableStateOf<ChatPopupState?>(null) }

    val localAnswer = viewModel.answer.collectAsStateWithLifecycle()
    val chatHistory = viewModel.chatHistory.collectAsStateWithLifecycle(emptyList())

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
            reverseLayout = true,
        ) {
            localAnswer.value.let {
                when (it) {
                    is ChatResult.Progress -> item { ReceiveText(it.data.content) }
                    is ChatResult.Error -> item { ReceiveText(it.e.message.orEmpty()) }
                    else -> {}
                }
            }
            items(chatHistory.value) { chat ->
                ChatBox(chat, popupState)
            }
        }
        Card(
            modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Row {
                TextField(
                    value = text,
                    modifier = Modifier.weight(1f),
                    maxLines = 4,
                    label = { Text(stringResource(Res.string.enter_question)) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
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
                        containerColor = Color.Black,
                        disabledContainerColor = Color.Gray,
                    ),
                    enabled = text.text.isNotBlank() && localAnswer.value !is ChatResult.Progress,
                    onClick = {
                        if (text.text.isNotBlank()) {
                            focusManager.clearFocus()
                            viewModel.sendData(text.text)
                            text = TextFieldValue("")
                        }
                    },
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp).rotate(270f),
                        painter = painterResource(Res.drawable.ic_send),
                        tint = Color.White,
                        contentDescription = "send",
                    )
                }
            }
        }
    }
    ChatPopup(popupState, viewModel::doEvent)
}

@Composable
private fun ReceiveText(content: String, modifier: Modifier = Modifier) {
    Text(content, modifier = modifier.padding(bottom = 10.dp))
}

@Composable
private fun ChatBox(chat: ChatHistory, popupState: MutableState<ChatPopupState?>) {
    chat.receive?.let {
        var receiveCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
        ReceiveText(
            it.content,
            Modifier
                .onGloballyPositioned { cor -> receiveCoordinates = cor }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { offset ->
                            popupState.value = ChatPopupState(
                                offset = receiveCoordinates?.localToWindow(offset)?.round()
                                    ?: IntOffset(0, 0),
                                history = chat,
                                type = ChatType.RECEIVER,
                            )
                        }
                    )
                }
        )
    }
    chat.send?.let {
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
                                history = chat,
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
                    .background(CommonColor.ChatBoxBg)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
