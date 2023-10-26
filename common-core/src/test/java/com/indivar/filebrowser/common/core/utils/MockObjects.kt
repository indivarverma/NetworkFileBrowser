package com.indivar.filebrowser.common.core.utils

import com.indivar.filebrowser.models.FileSystemElement
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

fun createMockedDirectory(
    identifier: String? = null,
    parentIdentifier: String? = null,
    dirname: String? = null
) =
    mockk<FileSystemElement.Directory>(relaxed = true).apply {
        every { id }.returns(identifier ?: UUID.randomUUID().toString())
        every { parentId }.returns(parentIdentifier ?: UUID.randomUUID().toString())
        every { name }.returns(dirname ?: UUID.randomUUID().toString())
    }

fun createMockedFile(
    identifier: String? = null,
    parentIdentifier: String? = null,
    filename: String? = null,
    fileContentType: String? = null,
) =
    mockk<FileSystemElement.File>(relaxed = true).apply {
        every { id }.returns(identifier ?: UUID.randomUUID().toString())
        every { parentId }.returns(parentIdentifier ?: UUID.randomUUID().toString())
        every { name }.returns(filename ?: UUID.randomUUID().toString())
        every { contentType }.returns(fileContentType ?: UUID.randomUUID().toString())
    }
