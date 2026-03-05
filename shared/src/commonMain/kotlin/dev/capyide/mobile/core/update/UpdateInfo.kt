package dev.capyide.mobile.core.update

data class UpdateInfo(
    val latestVersionCode: Int,
    val latestVersionName: String,
    val apkUrl: String,
    val changelog: String
)
