package dev.capyide.mobile.core.platform

actual object Platform {
    actual val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}
