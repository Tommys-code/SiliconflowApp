package com.tommy.siliconflow.app.ui.compose.imageCreation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tommy.siliconflow.app.data.ImageRatio
import com.tommy.siliconflow.app.data.db.ImageData
import com.tommy.siliconflow.app.ui.components.ImageItem
import com.tommy.siliconflow.app.ui.components.NormalButton
import com.tommy.siliconflow.app.ui.theme.AppColor
import com.tommy.siliconflow.app.ui.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_keyboard_arrow_down

@Composable
fun ImageCreatedView(imageData: ImageData, ratio: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        for (i in 0 until imageData.urls.size step 2) {
            Item(imageData.urls.getOrNull(i), imageData.urls.getOrNull(i + 1), ratio)
        }
    }
}

@Composable
private fun Item(first: String?, second: String?, ratio: String) {
    first?.let {
        Row(modifier = Modifier.fillMaxWidth()) {
            ImageItem(
                it,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(ImageRatio.fromValue(ratio).size)
            )
            second?.let {
                ImageItem(
                    it,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .aspectRatio(ImageRatio.fromValue(ratio).size)
                )

            }
        }
    }
}

@Composable
internal fun ImageConfigItem(
    content: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .border(
                BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            content,
            modifier = Modifier,
            color = AppTheme.colorScheme.primaryText,
            style = MaterialTheme.typography.bodyMedium,
        )
        Icon(
            modifier = Modifier.padding(start = 2.dp),
            painter = painterResource(Res.drawable.ic_keyboard_arrow_down),
            contentDescription = "choose",
            tint = AppTheme.colorScheme.primaryText,
        )
    }
}