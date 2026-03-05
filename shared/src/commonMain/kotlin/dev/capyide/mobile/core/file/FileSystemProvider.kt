package dev.capyide.mobile.core.file

expect class FileSystemProvider {
    fun listFiles(path: String): List<FileNode>
    fun readFile(path: String): String
    fun writeFile(path: String, content: String)
    fun createFile(path: String)
    fun createDirectory(path: String)
    fun delete(path: String)
    fun exists(path: String): Boolean
    fun getDefaultProjectPath(): String
}
