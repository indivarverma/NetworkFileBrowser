package com.indivar.filebrowser.network

import android.content.Context
import androidx.core.content.edit
import com.indivar.filebrowser.common.core.utils.PersistentStore

class PersistentStoreImpl(private val context: Context) : PersistentStore {
    private val sharedPreferences by lazy {
        context.getSharedPreferences("filebrowser", Context.MODE_PRIVATE)
    }

    override fun getString(key: String, defaultValue: String?): String? =
        sharedPreferences.getString(key, defaultValue)

    override fun putString(key: String, value: String?) {
        sharedPreferences.edit(commit = true) {
            this.putString(key, value)
        }
    }
}