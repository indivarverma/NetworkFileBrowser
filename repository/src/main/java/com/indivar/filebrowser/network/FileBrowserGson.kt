package com.indivar.filebrowser.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.joda.time.DateTime

object FileBrowserGson {
    fun create(adapter: JodaDateTimeAdapter): Gson =
        GsonBuilder().registerTypeAdapter(
            DateTime::class.java,
            adapter
        ).create()

}