package dev.capyide.mobile.core.update

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class GithubUpdateChecker(
    private val client: HttpClient,
    private val owner: String = "StressTestor",
    private val repo: String = "CapyIDE-Mobile"
) : UpdateChecker {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun checkForUpdate(): UpdateInfo? {
        val response = client.get("https://api.github.com/repos/$owner/$repo/releases/latest") {
            header("Accept", "application/vnd.github+json")
        }
        if (!response.status.isSuccess()) return null
        val body = response.bodyAsText()
        val release = json.parseToJsonElement(body).jsonObject
        return parseRelease(release)
    }

    private fun parseRelease(release: JsonObject): UpdateInfo? {
        val rawVersion = release["tag_name"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() }
            ?: release["name"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() }
            ?: return null

        val assets = release["assets"]?.jsonArray ?: return null
        val apkUrl = assets
            .mapNotNull { it.jsonObject["browser_download_url"]?.jsonPrimitive?.content }
            .firstOrNull { it.endsWith(".apk") }
            ?: return null

        val versionName = rawVersion.removePrefix("v").trim()
        return UpdateInfo(
            latestVersionCode = versionNameToCode(versionName),
            latestVersionName = versionName,
            apkUrl = apkUrl,
            changelog = release["body"]?.jsonPrimitive?.content ?: ""
        )
    }

    companion object {
        fun versionNameToCode(versionName: String): Int {
            val parts = versionName.split(".")
                .mapNotNull { segment ->
                    val numeric = segment.takeWhile { it.isDigit() }
                    numeric.takeIf { it.isNotEmpty() }?.toInt()
                }
            if (parts.isEmpty()) return 1
            return parts.fold(0) { acc, value -> acc * 100 + value }.coerceAtLeast(1)
        }
    }
}
