package com.tommy.siliconflow.app.platform

import android.content.Context
import android.content.pm.PackageInfo
import androidx.compose.runtime.Composable
import coil3.compose.LocalPlatformContext

actual object AppInfo {

    private val context: Context
        @Composable get() = LocalPlatformContext.current

    private val packageInfo: PackageInfo
        @Composable get() = context.packageManager.getPackageInfo(context.packageName, 0)

    actual val versionName: String
        @Composable get() = packageInfo.versionName.orEmpty()

    actual val versionCode: Long
        @Composable get() = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
}