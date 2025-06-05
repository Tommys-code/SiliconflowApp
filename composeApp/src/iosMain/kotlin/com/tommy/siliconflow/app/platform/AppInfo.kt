package com.tommy.siliconflow.app.platform

import platform.Foundation.NSBundle

actual object AppInfo {
    actual val versionName: String =
        NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as String

    actual val versionCode: Long =
        (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as String).toLong()
}
