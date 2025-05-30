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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.tommy.siliconflow.app.data.ImageRatio
import com.tommy.siliconflow.app.data.db.ImageData
import com.tommy.siliconflow.app.navigation.AppScreen
import com.tommy.siliconflow.app.ui.components.ImageItem
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.ImageCreationEvent
import org.jetbrains.compose.resources.painterResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_image
import siliconflowapp.composeapp.generated.resources.ic_keyboard_arrow_down

@Composable
fun ImageCreatedView(
    imageData: ImageData,
    ratio: String,
    doEvent: (ImageCreationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        val imageRatio = ImageRatio.fromValue(ratio)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp),
        ) {
            itemsIndexed(imageData.urls) { index, item ->
                ImageItem(
                    item,
                    modifier = Modifier
                        .clickable {
                            doEvent(
                                ImageCreationEvent.Navigate(
                                    AppScreen.ImagePreview(imageData.urls, index)
                                )
                            )
                        }
                        .fillParentMaxWidth(0.5f)
                        .aspectRatio(imageRatio.size),
                    placeholder = painterResource(Res.drawable.ic_image),
                )
            }
        }
    }
}

@Composable
internal fun ImageConfigItem(
    content: String,
    onClick: (IntOffset) -> Unit,
) {
    var position by remember { mutableStateOf(IntOffset.Zero) }
    Row(
        modifier = Modifier
            .border(
                BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                RoundedCornerShape(10.dp)
            )
            .onGloballyPositioned { cor -> position = cor.localToWindow(Offset.Zero).round() }
            .clickable { onClick(position) }
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