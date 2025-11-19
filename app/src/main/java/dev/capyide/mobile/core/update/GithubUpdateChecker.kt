package dev.capyide.mobile.core.update

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class GithubUpdateChecker(
    private val owner: String = "StressTestor",
    private val repo: String = "CapyIDE-Mobile",
    private val client: OkHttpClient = OkHttpClient()
) : UpdateChecker {

    override suspend fun checkForUpdate(): UpdateInfo? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .get()
            .url("https://api.github.com/repos/$owner/$repo/releases/latest")
            .header("Accept", "application/vnd.github+json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext null
            val body = response.body?.string() ?: return@withContext null
            parseRelease(JSONObject(body))
        }
    }

    private fun parseRelease(json: JSONObject): UpdateInfo? {
        val rawVersion = json.optString("tag_name")
            .takeUnless { it.isNullOrBlank() }
            ?: json.optString("name")

        if (rawVersion.isNullOrBlank()) return null

        val assets = json.optJSONArray("assets") ?: JSONArray()
        val apkUrl = (0 until assets.length())
            .asSequence()
            .mapNotNull { index ->
                assets.optJSONObject(index)?.optString("browser_download_url")
            }
            .firstOrNull { it.endsWith(".apk") }
            ?: return null

        val versionName = rawVersion.removePrefix("v").trim()
        return UpdateInfo(
            latestVersionCode = versionNameToCode(versionName),
            latestVersionName = versionName,
            apkUrl = apkUrl,
            changelog = json.optString("body")
        )
    }

    companion object {
        @VisibleForTesting
        fun versionNameToCode(versionName: String): Int {
            val parts = versionName.split(".")
                .mapNotNull { segment ->
                    val numeric = segment.takeWhile { it.isDigit() }
                    numeric.takeIf { it.isNotEmpty() }?.toInt()
                }

            if (parts.isEmpty()) {
                return 1
            }

            return parts.fold(0) { acc, value -> acc * 100 + value }
                .coerceAtLeast(1)
        }
    }
}
