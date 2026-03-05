package dev.capyide.mobile.ui.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformCodeEditor(
    content: String,
    language: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
)
