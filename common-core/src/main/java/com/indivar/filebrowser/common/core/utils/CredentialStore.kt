package com.indivar.filebrowser.common.core.utils

import com.indivar.filebrowser.models.FileBrowserCredentials

interface CredentialStore {
    fun getStoredCredentials(): FileBrowserCredentials?
    fun setStoredCredentials(credentials: FileBrowserCredentials?)
    fun clear()
}