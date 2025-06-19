package com.filipkampic.mindthrive.ui.habitTracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
fun FrequencyOptionRow(
    labelStart: String,
    labelEnd: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    inputValue: String,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors,
    keyboardType: KeyboardOptions
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = {},
            colors = RadioButtonDefaults.colors(selectedColor = Peach)
        )
        if (labelStart.isNotEmpty()) {
            Text(labelStart, color = Peach)
        }
        OutlinedTextField(
            value = inputValue,
            onValueChange = { onValueChange(it.filter(Char::isDigit)) },
            modifier = Modifier.width(60.dp),
            singleLine = true,
            keyboardOptions = keyboardType,
            textStyle = LocalTextStyle.current.copy(color = Peach),
            colors = colors
        )
        if (labelEnd.isNotEmpty()) {
            Text(labelEnd, color = Peach)
        }
    }
}
