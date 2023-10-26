package com.indivar.filebrowser.utils

fun (() -> Unit).runWith(function: () -> Unit): () -> Unit = {
    this()
    function()
}