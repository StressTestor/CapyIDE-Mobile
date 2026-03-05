package dev.capyide.mobile.ui.editor

data class EditorState(
    val content: String = "",
    val filePath: String? = null,
    val fileName: String = "untitled.kt",
    val language: String = "kotlin",
    val cursorLine: Int = 0,
    val cursorColumn: Int = 0,
    val isDirty: Boolean = false
)
