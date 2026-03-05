package dev.capyide.mobile.ui.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKScriptMessage
import platform.WebKit.WKScriptMessageHandlerProtocol
import platform.WebKit.WKUserContentController
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.javaScriptEnabled
import platform.CoreGraphics.CGRectMake
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
fun IosCodeEditorView(
    content: String,
    language: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val messageHandler = remember { EditorMessageHandler(onContentChange) }
    val webView = remember {
        val config = WKWebViewConfiguration().apply {
            val controller = userContentController
            controller.addScriptMessageHandler(messageHandler, "contentChanged")
            controller.addScriptMessageHandler(messageHandler, "editorReady")
            preferences.javaScriptEnabled = true
        }
        WKWebView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0), configuration = config).apply {
            scrollView.bounces = false
            scrollView.scrollEnabled = false
            setOpaque(false)

            // Load the editor HTML from bundle resources
            // Try compose resource path first, then fallback paths
            val htmlPath = NSBundle.mainBundle.pathForResource("editor", ofType = "html", inDirectory = "composeResources/capyide_mobile.composeapp.generated.resources/files/codemirror")
                ?: NSBundle.mainBundle.pathForResource("editor", ofType = "html", inDirectory = "compose-resources/files/codemirror")
            if (htmlPath != null) {
                val url = NSURL.fileURLWithPath(htmlPath)
                val dirUrl = url.URLByDeletingLastPathComponent
                if (dirUrl != null) {
                    loadFileURL(url, allowingReadAccessToURL = dirUrl)
                }
            } else {
                // Fallback: load from compose resources path
                val bundlePath = NSBundle.mainBundle.resourcePath ?: ""
                val fallbackHtml = """
                    <!DOCTYPE html><html><body style="font-family:monospace;padding:16px;color:#ccc;background:#1D1A2B;">
                    <p>Editor loading... If this persists, resources may not be bundled correctly.</p>
                    </body></html>
                """.trimIndent()
                loadHTMLString(fallbackHtml, baseURL = null)
            }
        }
    }

    // Sync content and language when they change
    LaunchedEffect(content) {
        if (messageHandler.isReady) {
            val escaped = content.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "")
            webView.evaluateJavaScript("window.editorAPI.setContent('$escaped')", completionHandler = null)
        }
    }

    LaunchedEffect(language) {
        if (messageHandler.isReady) {
            webView.evaluateJavaScript("window.editorAPI.setLanguage('$language')", completionHandler = null)
        }
    }

    UIKitView(
        factory = { webView },
        modifier = modifier
    )

    DisposableEffect(Unit) {
        onDispose {
            val controller = webView.configuration.userContentController
            controller.removeScriptMessageHandlerForName("contentChanged")
            controller.removeScriptMessageHandlerForName("editorReady")
        }
    }
}

private class EditorMessageHandler(
    private val onContentChange: (String) -> Unit
) : NSObject(), WKScriptMessageHandlerProtocol {

    var isReady = false
        private set

    override fun userContentController(
        userContentController: WKUserContentController,
        didReceiveScriptMessage: WKScriptMessage
    ) {
        when (didReceiveScriptMessage.name) {
            "editorReady" -> {
                isReady = true
            }
            "contentChanged" -> {
                val body = didReceiveScriptMessage.body as? String ?: return
                onContentChange(body)
            }
        }
    }
}
