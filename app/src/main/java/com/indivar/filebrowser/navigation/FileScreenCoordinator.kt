package com.indivar.filebrowser.navigation

interface FileScreenCoordinator {
    fun onFileItemClicked(id: String, isImage: Boolean)
    fun logout()
}
