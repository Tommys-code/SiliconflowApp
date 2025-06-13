package com.tommy.siliconflow.app.platform

import coil3.Image
import coil3.PlatformContext

expect fun Image.toByteArray(): ByteArray?

expect fun saveToLocal(context: PlatformContext, data: ByteArray, name: String): String?

expect fun String.getUri(): String
