package com.calculatorlife.app.ui.vault

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap

/** Best-effort file extension for a picked media Uri, defaulting sensibly if the type is unknown. */
fun guessExtension(context: Context, uri: Uri, fallback: String): String {
    val mimeType = context.contentResolver.getType(uri)
    val fromMime = mimeType?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
    return fromMime ?: fallback
}
