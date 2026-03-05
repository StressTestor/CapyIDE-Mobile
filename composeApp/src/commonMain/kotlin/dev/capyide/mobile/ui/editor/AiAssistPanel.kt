package dev.capyide.mobile.ui.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun AiAssistPanel(
    selectedCode: String,
    isLoading: Boolean,
    response: String,
    onExplain: (String) -> Unit,
    onComplete: (String) -> Unit,
    onRefactor: (String) -> Unit,
    onChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var chatInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (selectedCode.isNotBlank()) {
            Text(
                text = "Selected code:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = selectedCode.take(200) + if (selectedCode.length > 200) "..." else "",
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { onExplain(selectedCode) }, modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                    Text("Explain")
                }
                Button(onClick = { onComplete(selectedCode) }, modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                    Text("Complete")
                }
                Button(onClick = { onRefactor(selectedCode) }, modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                    Text("Refactor")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = chatInput,
            onValueChange = { chatInput = it },
            label = { Text("Ask AI...") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (chatInput.isNotBlank()) {
                    onChat(chatInput)
                    chatInput = ""
                }
            },
            enabled = chatInput.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.height(20.dp))
            } else {
                Text("Send")
            }
        }

        if (response.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = response,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}
