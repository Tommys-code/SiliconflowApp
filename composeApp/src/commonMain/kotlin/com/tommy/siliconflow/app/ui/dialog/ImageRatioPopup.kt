package com.tommy.siliconflow.app.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tommy.siliconflow.app.data.ImageRatio
import com.tommy.siliconflow.app.extensions.topPosition
import com.tommy.siliconflow.app.ui.components.CustomPopup
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.ImageCreationEvent
import org.jetbrains.compose.resources.painterResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_check

@Stable
data class ImageRatioPopupState(
    val offset: IntOffset = IntOffset(0, 0),
    val selected: ImageRatio,
)

@Composable
fun ImageRatioPopup(
    state: MutableState<ImageRatioPopupState?>,
    doEvent: (ImageCreationEvent) -> Unit,
) {
    state.value?.let {
        CustomPopup(
            onDismissRequest = { state.value = null },
            calculatePosition = it.offset.topPosition,
        ) {
            Column(
                modifier = Modifier
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(12.dp))
                    .width(150.dp)
                    .background(AppTheme.colorScheme.popContainer),
            ) {
                ImageRatio.entries.forEachIndexed { index, entry ->
                    RatioItem(entry, entry == it.selected) {
                        doEvent(ImageCreationEvent.UpdateRatio(entry))
                        state.value = null
                    }
                    if (index != ImageRatio.entries.size - 1) {
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun RatioItem(ratio: ImageRatio, isSelected: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(ratio.desc) },
        leadingIcon = {
            Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier.border(
                        width = 2.dp,
                        color = AppTheme.colorScheme.iconContainer,
                        shape = RoundedCornerShape(2.dp),
                    ).aspectRatio(ratio.size)
                )
            }
        },
        trailingIcon = {
            if (isSelected) {
                Icon(
                    painter = painterResource(Res.drawable.ic_check),
                    contentDescription = "check",
                    modifier = Modifier.size(20.dp),
                )
            }
        },
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    )
}