package com.tommy.siliconflow.app.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.siliconflow.app.ui.components.CustomTopBar
import com.tommy.siliconflow.app.ui.theme.CommonColor
import com.tommy.siliconflow.app.viewmodel.ModelListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ModelListScreen(
    popBack: () -> Unit,
    viewModel: ModelListViewModel = koinViewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopBar(
                title = "",
                popBack = popBack,
            )
        }
    ) { innerPadding ->
        viewModel.modelList.collectAsStateWithLifecycle(emptyList()).value.let {
            val currentModel = viewModel.currentModel.collectAsStateWithLifecycle(null).value
            LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                items(it) { model ->
                    Text(
                        model.model,
                        modifier = Modifier
                            .clickable {
                                viewModel.changeModel(model)
                                popBack()
                            }
                            .background(
                                if (model == currentModel) CommonColor.TinyLightGray else Color.Transparent
                            )
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp)
                    )
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}