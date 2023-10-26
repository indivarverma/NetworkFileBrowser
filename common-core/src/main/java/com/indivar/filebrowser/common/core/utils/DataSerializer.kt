package com.indivar.filebrowser.common.core.utils

import java.lang.reflect.Type

interface DataSerializer {
    fun serialize(value: Any): String

    fun<T> deserialize(value: String, type: Type): T?
}