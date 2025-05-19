package com.tommy.siliconflow.app.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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
    val localAnswer = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            state = listState
        ) {
            item {
                Text(localAnswer.value)
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
                    }
                })
            )
        }
    }

    LaunchedEffect(viewModel.answer) {
        viewModel.answer.collect { value ->
            localAnswer.value += value // 安全更新本地状态
        }
    }
}