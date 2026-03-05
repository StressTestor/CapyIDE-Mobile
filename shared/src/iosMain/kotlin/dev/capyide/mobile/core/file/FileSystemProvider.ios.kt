@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package dev.capyide.mobile.core.file

import platform.Foundation.NSFileManager
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSURL
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.writeToFile

actual class FileSystemProvider {

    private val fileManager = NSFileManager.defaultManager

    actual fun listFiles(path: String): List<FileNode> {
        val contents = fileManager.contentsOfDirectoryAtPath(path, error = null) ?: return emptyList()
        @Suppress("UNCHECKED_CAST")
        val items = contents as List<String>
        return items.map { name ->
            val fullPath = "$path/$name"
            FileNode(
                name = name,
                path = fullPath,
                isDirectory = isDirectoryCheck(fullPath),
                children = emptyList()
            )
        }.sortedWith(compareByDescending<FileNode> { it.isDirectory }.thenBy { it.name })
    }

    private fun isDirectoryCheck(path: String): Boolean {
        val contents = fileManager.contentsOfDirectoryAtPath(path, error = null)
        return contents != null
    }

    actual fun readFile(path: String): String {
        return NSString.stringWithContentsOfFile(path, encoding = NSUTF8StringEncoding, error = null) ?: ""
    }

    actual fun writeFile(path: String, content: String) {
        (content as NSString).writeToFile(path, atomically = true, encoding = NSUTF8StringEncoding, error = null)
    }

    actual fun createFile(path: String) {
        fileManager.createFileAtPath(path, contents = null, attributes = null)
    }

    actual fun createDirectory(path: String) {
        fileManager.createDirectoryAtPath(path, withIntermediateDirectories = true, attributes = null, error = null)
    }

    actual fun delete(path: String) {
        fileManager.removeItemAtPath(path, error = null)
    }

    actual fun exists(path: String): Boolean {
        return fileManager.fileExistsAtPath(path)
    }

    actual fun getDefaultProjectPath(): String {
        val urls = fileManager.URLsForDirectory(NSDocumentDirectory, inDomains = NSUserDomainMask)
        val docUrl = urls.firstOrNull() as? NSURL
        return (docUrl?.path ?: "") + "/projects"
    }
}
