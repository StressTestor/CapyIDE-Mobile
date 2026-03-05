package dev.capyide.mobile.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EditorFileState(
    val content: String = "",
    val filePath: String? = null,
    val fileName: String = "untitled.kt",
    val language: String = "kotlin",
    val isDirty: Boolean = false,
    val openTabs: List<String> = emptyList(),
    val activeTabIndex: Int = 0
)

class EditorViewModel {

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(EditorFileState())
    val state: StateFlow<EditorFileState> = _state.asStateFlow()

    fun updateContent(content: String) {
        _state.update { it.copy(content = content, isDirty = true) }
    }

    fun openFile(path: String, content: String) {
        val ext = path.substringAfterLast('.', "txt")
        val lang = extensionToLanguage(ext)
        val name = path.substringAfterLast('/')
        _state.update {
            it.copy(
                content = content,
                filePath = path,
                fileName = name,
                language = lang,
                isDirty = false
            )
        }
    }

    fun markSaved() {
        _state.update { it.copy(isDirty = false) }
    }

    fun newFile() {
        _state.update {
            EditorFileState()
        }
    }

    companion object {
        fun extensionToLanguage(ext: String): String = when (ext.lowercase()) {
            "kt", "kts" -> "kotlin"
            "java" -> "java"
            "xml" -> "xml"
            "js", "jsx" -> "javascript"
            "ts", "tsx" -> "typescript"
            "py" -> "python"
            "json" -> "json"
            "html", "htm" -> "html"
            "css" -> "css"
            "md", "markdown" -> "markdown"
            else -> "text"
        }
    }
}
