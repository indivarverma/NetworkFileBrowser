package com.indivar.filebrowser.navigation

class FileScreenCoordinatorImpl(
    private val navigator: FileScreenNavigator
) : FileScreenCoordinator {

    override fun onFileItemClicked(id: String, isImage: Boolean) {
        if (isImage) {
            navigator.openImageFile(id)
        }
    }

    override fun logout() {
        navigator.logout()
    }
}
