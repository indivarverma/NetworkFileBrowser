package com.indivar.filebrowser.common.core.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.indivar.filebrowser.models.FileBrowserCredentials
import io.mockk.every
import java.util.concurrent.CountDownLatch

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun CredentialStore.setReturnValue(credentials: FileBrowserCredentials?) {
    every { getStoredCredentials() }.returns(credentials)
}

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    countDownStart: Int = 1,
): T {
    var data: T? = null
    val latch = CountDownLatch(countDownStart)
    val observer = object : Observer<T> {
        override fun onChanged(o: T) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value absent")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}