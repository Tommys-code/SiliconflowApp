package com.tommy.siliconflow.app.ui.dialog

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.tommy.siliconflow.app.navigation.AppScreen
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.MainViewEvent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.create_new_image
import siliconflowapp.composeapp.generated.resources.create_new_session
import siliconflowapp.composeapp.generated.resources.ic_chat
import siliconflowapp.composeapp.generated.resources.ic_image

@Composable
fun ChooseCreationPopup(
    expand: MutableState<Boolean>,
    doEvent: (MainViewEvent) -> Unit,
) {
    DropdownMenu(
        expanded = expand.value,
        onDismissRequest = { expand.value = false },
        containerColor = AppTheme.colorScheme.popContainer,
        offset = DpOffset((-10).dp, 0.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(Res.string.create_new_session)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_chat),
                    contentDescription = "chat",
                    modifier = Modifier.size(20.dp),
                )
            },
            onClick = {
                doEvent(MainViewEvent.ChangeSession(null))
                expand.value = false
            }
        )
        HorizontalDivider(thickness = 0.5.dp)
        DropdownMenuItem(
            text = { Text(stringResource(Res.string.create_new_image)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_image),
                    contentDescription = "chat",
                    modifier = Modifier.size(20.dp),
                )
            },
            onClick = {
                doEvent(MainViewEvent.Navigate(AppScreen.ImageCreation(null)))
                expand.value = false
            }
        )
    }
}