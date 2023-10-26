package com.indivar.filebrowser.common.core.utils

import android.util.Base64
import java.nio.charset.Charset

class Base64EncoderImpl: Base64Encoder {
    override fun encode(value: String): String {
        val ct = value.toByteArray(Charset.forName("UTF-8"))
        return Base64.encodeToString(ct, Base64.NO_WRAP)
    }
}