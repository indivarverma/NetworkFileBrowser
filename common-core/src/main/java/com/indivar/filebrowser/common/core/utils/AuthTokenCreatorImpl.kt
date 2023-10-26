package com.indivar.filebrowser.common.core.utils

class AuthTokenCreatorImpl(
    private val base64Encoder: Base64Encoder,
):AuthTokenCreator {
    override fun createAuthToken(username: String, password: String) =
        "Basic ${base64Encoder.encode("$username:$password")}"
}