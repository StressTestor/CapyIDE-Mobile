package dev.capyide.mobile.ui.editor

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.text.Content

@Composable
fun SoraEditorView(
    content: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val editor = remember { mutableMapOf<String, CodeEditor>() }

    AndroidView(
        factory = { context: Context ->
            CodeEditor(context).apply {
                setText(content)
                setTextSize(14f)
                editor["instance"] = this
            }
        },
        update = { codeEditor ->
            val currentText = codeEditor.text.toString()
            if (currentText != content) {
                codeEditor.setText(content)
            }
        },
        modifier = modifier
    )

    DisposableEffect(Unit) {
        onDispose {
            editor["instance"]?.release()
        }
    }
}
