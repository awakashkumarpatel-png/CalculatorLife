package com.calculatorlife.app.ui.calculator.standard

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

enum class KeyStyle { NUMBER, OPERATOR, EQUALS, FUNCTION }

@Composable
fun CalculatorKey(
    label: String,
    style: KeyStyle,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val backgroundColor = when (style) {
        KeyStyle.NUMBER -> MaterialTheme.colorScheme.surfaceVariant
        KeyStyle.OPERATOR -> MaterialTheme.colorScheme.primaryContainer
        KeyStyle.EQUALS -> MaterialTheme.colorScheme.primary
        KeyStyle.FUNCTION -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (style) {
        KeyStyle.EQUALS -> MaterialTheme.colorScheme.onPrimary
        KeyStyle.OPERATOR -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(color = backgroundColor, shape = CircleShape)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onLongClick = onLongClick,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = contentColor,
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
