package com.tommy.siliconflow.app.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.siliconflow.app.data.ChatResult
import com.tommy.siliconflow.app.ui.theme.CommonColor
import com.tommy.siliconflow.app.viewmodel.MainViewModel
import org.jetbrains.compose.resources.stringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.enter_question

@Composable
internal fun ChatView(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
) {
    val ques = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    val localAnswer = viewModel.answer.collectAsStateWithLifecycle()
    val chatHistory = viewModel.chatHistory.collectAsStateWithLifecycle(emptyList())

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
            state = listState
        ) {
            items(chatHistory.value) { chat ->
                chat.send?.let {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Text(
                            it.content,
                            modifier = Modifier
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
                chat.receive?.let {
                    Text(it.content)
                }
            }
            localAnswer.value.let {
                if (it is ChatResult.Progress) {
                    item { Text(it.data.content) }
                }
            }
        }
        Card(
            modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            TextField(
                value = ques.value,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(stringResource(Res.string.enter_question)) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                onValueChange = {
                    ques.value = it
                },
                keyboardActions = KeyboardActions(onSend = {
                    if (ques.value.isNotBlank()) {
                        focusManager.clearFocus()
                        viewModel.sendData(ques.value)
                        ques.value = ""
                    }
                })
            )
        }
    }
}