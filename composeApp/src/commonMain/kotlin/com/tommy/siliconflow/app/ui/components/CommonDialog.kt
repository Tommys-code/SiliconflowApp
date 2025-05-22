package com.tommy.siliconflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun CommonDialog(
    title: String,
    onDismissRequest: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
    button: @Composable RowScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(vertical = 12.dp)
            )
            content()
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                button()
            }
        }
    }
}