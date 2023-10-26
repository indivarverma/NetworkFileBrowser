package com.indivar.filebrowser.common.core.flash

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.indivar.filebrowser.common.core.MainCoroutineRule
import com.indivar.filebrowser.common.core.utils.CredentialStore
import com.indivar.filebrowser.common.core.utils.getOrAwaitValue
import com.indivar.filebrowser.common.core.utils.setReturnValue
import com.indivar.filebrowser.models.FileBrowserCredentials
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class FlashScreenViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var credentialStore: CredentialStore

    @Before
    fun init() {
        credentialStore = mockk<CredentialStore>(relaxed = true)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) }.returns(0)
    }

    @Test
    fun `Confirm ViewState is set as Authorized when credentialstore has saved credentials`() =
        runTest {
            val mockedCredentials = mockk<FileBrowserCredentials>()
            credentialStore.setReturnValue(mockedCredentials)
            val delegateToTest = FlashScreenViewModelDelegate(
                credentialStore
            )
            val viewmodel = FlashScreenViewModel(delegateToTest)
            val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertEquals(ViewState.HasAuthorization, value1)
        }

    @Test
    fun `Confirm ViewState is set as unauthorized when credentialstore has no credentials`() =
        runTest {
            credentialStore.setReturnValue(null)
            val delegateToTest = FlashScreenViewModelDelegate(
                credentialStore
            )
            val viewmodel = FlashScreenViewModel(delegateToTest)
            val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertEquals(ViewState.NotAuthorized, value1)
        }
}