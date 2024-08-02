package dev.datlag.mimasu.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@Composable
fun SearchView(
    query: String
) {
    Column {
        Text(
            text = query.ifBlank { "Start typing to search" },
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(all = 12.dp)
        )
        Spacer(modifier = Modifier.height(1.dp).background(MaterialTheme.colorScheme.onSurface))
    }
}