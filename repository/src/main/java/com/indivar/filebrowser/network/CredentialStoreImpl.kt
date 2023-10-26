package com.indivar.filebrowser.network

import com.indivar.filebrowser.common.core.utils.CredentialStore
import com.indivar.filebrowser.common.core.utils.PersistentStore
import com.indivar.filebrowser.models.FileBrowserCredentials
import java.util.concurrent.atomic.AtomicReference

class CredentialStoreImpl(private val persistentStore: PersistentStore) : CredentialStore {
    private val saved = AtomicReference<FileBrowserCredentials?>()

    init {
        load()
    }

    override fun getStoredCredentials(): FileBrowserCredentials? = saved.get()

    override fun setStoredCredentials(credentials: FileBrowserCredentials?) {
        persistentStore.putString(KEY_CREDENTIALS, credentials?.encodedCredentials)
        saved.set(credentials)
    }

    private fun load(): FileBrowserCredentials? =
        persistentStore.getString(KEY_CREDENTIALS, null)?.let { creds ->
            FileBrowserCredentials(creds).also {
                saved.set(it)
            }
        }

    override fun clear() {
        saved.set(null)
        setStoredCredentials(null)
    }

    companion object {
        const val KEY_CREDENTIALS = "KEY_CREDENTIALS"
    }
}