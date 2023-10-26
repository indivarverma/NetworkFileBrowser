package com.indivar.filebrowser.common.core.utils

interface AuthTokenCreator {
    fun createAuthToken(username: String, password: String): String
}