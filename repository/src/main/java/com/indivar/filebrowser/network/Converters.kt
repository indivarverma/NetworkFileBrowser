package com.indivar.filebrowser.network

import com.google.gson.Gson
import com.indivar.filebrowser.common.core.utils.DataSerializer
import java.lang.reflect.Type

class DataSerializerImpl(
    private val gson: Gson,
) : DataSerializer {
    override fun serialize(value: Any): String =try {
        gson.toJson(value)
    } catch (_: Exception) {
        ""
    }

    override fun <T> deserialize(value: String, type: Type) = try {
        gson.fromJson<T>(value, type)
    } catch (_: Exception) {
        null
    }
}
