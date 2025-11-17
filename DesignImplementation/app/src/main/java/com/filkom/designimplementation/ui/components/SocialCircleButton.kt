package com.filkom.designimplementation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.filkom.designimplementation.ui.theme.BorderClr

@Composable
public fun SocialCircleButton(iconRes: Int, contentDesc: String, onClick: () -> Unit) {
    OutlinedIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.outlinedIconButtonColors(),
        modifier = Modifier
            .size(48.dp)
            .border(
                width = 1.dp,
                color = BorderClr,
                shape = CircleShape
            )
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDesc,
            tint = Color.Unspecified,
            modifier = Modifier.size(22.dp)
        )
    }
}