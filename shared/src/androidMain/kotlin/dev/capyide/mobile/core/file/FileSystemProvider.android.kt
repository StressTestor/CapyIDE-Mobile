package dev.capyide.mobile.core.file

import android.content.Context
import java.io.File

actual class FileSystemProvider(private val context: Context) {

    actual fun listFiles(path: String): List<FileNode> {
        val dir = File(path)
        if (!dir.isDirectory) return emptyList()
        return dir.listFiles()?.map { file ->
            FileNode(
                name = file.name,
                path = file.absolutePath,
                isDirectory = file.isDirectory,
                children = if (file.isDirectory) listFiles(file.absolutePath) else emptyList()
            )
        }?.sortedWith(compareByDescending<FileNode> { it.isDirectory }.thenBy { it.name })
            ?: emptyList()
    }

    actual fun readFile(path: String): String {
        return File(path).readText()
    }

    actual fun writeFile(path: String, content: String) {
        File(path).writeText(content)
    }

    actual fun createFile(path: String) {
        File(path).createNewFile()
    }

    actual fun createDirectory(path: String) {
        File(path).mkdirs()
    }

    actual fun delete(path: String) {
        File(path).deleteRecursively()
    }

    actual fun exists(path: String): Boolean {
        return File(path).exists()
    }

    actual fun getDefaultProjectPath(): String {
        return context.filesDir.absolutePath + "/projects"
    }
}
