package dev.capyide.mobile.ui.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.capyide.mobile.core.file.FileNode

@Composable
fun FileTreePanel(
    files: List<FileNode>,
    onFileClick: (FileNode) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(files) { node ->
            FileTreeItem(node = node, depth = 0, onFileClick = onFileClick)
        }
    }
}

@Composable
private fun FileTreeItem(
    node: FileNode,
    depth: Int,
    onFileClick: (FileNode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (node.isDirectory) {
                    expanded = !expanded
                } else {
                    onFileClick(node)
                }
            }
            .padding(start = (depth * 16).dp, top = 4.dp, bottom = 4.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (node.isDirectory) {
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = node.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = if (node.isDirectory) 4.dp else 24.dp)
        )
    }

    if (expanded && node.isDirectory) {
        Column {
            node.children.forEach { child ->
                FileTreeItem(node = child, depth = depth + 1, onFileClick = onFileClick)
            }
        }
    }
}
