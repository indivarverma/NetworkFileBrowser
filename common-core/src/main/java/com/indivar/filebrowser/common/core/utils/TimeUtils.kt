package com.indivar.filebrowser.common.core.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object TimeUtils {
    val DateTime?.printableTime: String
        get() {
            this ?: return ""
            return DateTimeFormat.forPattern("MMM dd, YYYY HH:mm:ss").print(this)
        }
}