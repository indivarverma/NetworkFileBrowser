package com.indivar.filebrowser.common.core.utils

interface PersistentStore {
    fun getString(key: String, defaultValue: String?): String?
    fun putString(key: String, value: String?)
}