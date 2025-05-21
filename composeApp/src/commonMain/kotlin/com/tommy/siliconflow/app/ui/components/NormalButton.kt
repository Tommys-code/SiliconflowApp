package com.tommy.siliconflow.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tommy.siliconflow.app.ui.theme.CommonColor
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NormalButton(
    res: StringResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    enable: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(size = 10.dp),
    color: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = CommonColor.LightGray
    ),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    border: BorderStroke? = null,
) {
    NormalButton(
        stringResource(res),
        onClick,
        modifier,
        textColor,
        enable,
        shape,
        color,
        contentPadding,
        border
    )
}

@Composable
fun NormalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    enable: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(size = 10.dp),
    color: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = CommonColor.LightGray
    ),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    border: BorderStroke? = null,
) {
    Button(
        modifier = modifier, onClick = onClick,
        shape = shape,
        colors = color,
        enabled = enable,
        border = border,
        contentPadding = contentPadding,
    ) {
        Text(
            text,
            color = textColor,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}