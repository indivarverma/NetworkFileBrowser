package com.indivar.filebrowser.common.core.utils

interface UserAccountStore {
    fun geFirstName(): String?
    fun getLastName(): String?
    fun getUserId(): String?
    fun setFirstName(value: String?)
    fun setLastName(value: String?)
    fun setUserId(value: String?)
    fun getTopDirectory(): String?
    fun setTopDirectory(value: String?)
    fun clear()

}