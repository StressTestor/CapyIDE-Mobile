package dev.capyide.mobile.core.platform

import platform.UIKit.UIDevice

actual object Platform {
    actual val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}
