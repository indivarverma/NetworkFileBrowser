package com.indivar.filebrowser.network

import com.indivar.filebrowser.common.core.utils.PersistentStore
import com.indivar.filebrowser.common.core.utils.UserAccountStore
import java.util.concurrent.atomic.AtomicReference

class UserAccountStoreImpl(
    private val persistentStore: PersistentStore
) : UserAccountStore {
    private val savedUserId = AtomicReference<String?>()
    private val savedFirstName = AtomicReference<String?>()
    private val savedLastName = AtomicReference<String?>()
    private val savedTopDirectory = AtomicReference<String?>()

    init {
        load()
    }

    private fun load() {
        persistentStore.getString(KEY_USER_ID, null)?.let { userId ->
            savedUserId.set(userId)
        }
        persistentStore.getString(KEY_USER_FIRST_NAME, null)?.let { firstName ->
            savedFirstName.set(firstName)
        }
        persistentStore.getString(KEY_USER_LAST_NAME, null)?.let { lastName ->
            savedLastName.set(lastName)
        }
        persistentStore.getString(KEY_USER_TOP_DIR, null)?.let { topDir ->
            savedTopDirectory.set(topDir)
        }
    }

    override fun geFirstName(): String? =
        savedFirstName.get()

    override fun getLastName(): String? =
        savedLastName.get()

    override fun getUserId(): String? =
        savedUserId.get()

    override fun setFirstName(value: String?) {
        savedFirstName.set(value)
        persistentStore.putString(KEY_USER_FIRST_NAME, value)
    }

    override fun setLastName(value: String?) {
        savedLastName.set(value)
        persistentStore.putString(KEY_USER_LAST_NAME, value)
    }

    override fun setUserId(value: String?) {
        savedUserId.set(value)
        persistentStore.putString(KEY_USER_ID, value)
    }

    override fun getTopDirectory(): String? = savedTopDirectory.get()

    override fun setTopDirectory(value: String?) {
        savedTopDirectory.set(value)
        persistentStore.putString(KEY_USER_TOP_DIR, value)
    }

    override fun clear() {
        setFirstName(null)
        setLastName(null)
        setUserId(null)
        setTopDirectory(null)
    }

    companion object {
        const val KEY_USER_FIRST_NAME = "KEY_USER_FIRST_NAME"
        const val KEY_USER_LAST_NAME = "KEY_USER_LAST_NAME"
        const val KEY_USER_TOP_DIR = "KEY_USER_TOP_DIR"
        const val KEY_USER_ID = "KEY_USER_ID"
    }
}