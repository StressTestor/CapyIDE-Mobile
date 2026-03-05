package dev.capyide.mobile.ui.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformCodeEditor(
    content: String,
    language: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier
) {
    IosCodeEditorView(
        content = content,
        language = language,
        onContentChange = onContentChange,
        modifier = modifier
    )
}
