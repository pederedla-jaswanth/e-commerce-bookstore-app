package com.example.ebookstore.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ebookstore.ui.theme.EBookStoreSpacing
import com.example.ebookstore.ui.theme.EBookStoreTheme
import com.example.ebookstore.ui.theme.ThemeMode

/**
 * Compact quantity stepper: [−] count [+].
 *
 * - Decrement is disabled when [quantity] == 1 (prevents going below 1).
 * - Colors are fully token-based — no hardcoded values.
 * - Outlined container uses [MaterialTheme.colorScheme.outline].
 *
 * @param quantity    Current quantity to display.
 * @param onIncrease  Called when the + button is tapped.
 * @param onDecrease  Called when the − button is tapped (disabled at quantity == 1).
 * @param modifier    Applied to the outer [Row].
 */
@Composable
fun QuantityStepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraLarge,
            )
            .padding(horizontal = EBookStoreSpacing.XXSmall),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        // Decrease button
        IconButton(
            onClick  = onDecrease,
            enabled  = quantity > 1,
            modifier = Modifier
                .size(32.dp)
                .semantics { contentDescription = "Decrease quantity" },
        ) {
            Icon(
                imageVector        = Icons.Filled.Remove,
                contentDescription = null,
                tint               = if (quantity > 1)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier           = Modifier.size(16.dp),
            )
        }

        // Quantity label
        Text(
            text       = "$quantity",
            style      = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.onSurface,
            modifier   = Modifier
                .defaultMinSize(minWidth = 24.dp)
                .padding(horizontal = EBookStoreSpacing.XSmall)
                .semantics { contentDescription = "Quantity $quantity" },
        )

        // Increase button
        IconButton(
            onClick  = onIncrease,
            modifier = Modifier
                .size(32.dp)
                .semantics { contentDescription = "Increase quantity" },
        ) {
            Icon(
                imageVector        = Icons.Filled.Add,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(16.dp),
            )
        }
    }
}

@Preview(name = "Quantity Stepper — Light, qty 2")
@Composable
private fun QuantityStepperLightPreview() {
    EBookStoreTheme {
        QuantityStepper(quantity = 2, onIncrease = {}, onDecrease = {})
    }
}

@Preview(name = "Quantity Stepper — Dark, qty 1 (decrease disabled)")
@Composable
private fun QuantityStepperDarkPreview() {
    EBookStoreTheme(themeMode = ThemeMode.DARK) {
        QuantityStepper(quantity = 1, onIncrease = {}, onDecrease = {})
    }
}
