package com.indivar.filebrowser.common.core.login

import android.os.Looper
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.indivar.filebrowser.common.core.MainCoroutineRule
import com.indivar.filebrowser.common.core.utils.AuthTokenCreator
import com.indivar.filebrowser.common.core.utils.CredentialStore
import com.indivar.filebrowser.common.core.utils.DataSerializer
import com.indivar.filebrowser.common.core.utils.INetworkHandler
import com.indivar.filebrowser.common.core.utils.UserAccountStore
import com.indivar.filebrowser.common.core.utils.getOrAwaitValue
import com.indivar.filebrowser.common.core.utils.setReturnValue
import com.indivar.filebrowser.models.FileBrowserCredentials
import com.indivar.filebrowser.models.DetailedServerError
import com.indivar.filebrowser.models.UserDetails
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class LoginViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var credentialStore: CredentialStore
    private lateinit var userAccountStore: UserAccountStore
    private lateinit var networkHandler: INetworkHandler
    private lateinit var authTokenCreator: AuthTokenCreator
    private lateinit var dataSerializer: DataSerializer
    private lateinit var userDetails: UserDetails
    private lateinit var error: DetailedServerError


    @Before
    fun init() {
        credentialStore = mockk<CredentialStore>(relaxed = true)
        userAccountStore = mockk<UserAccountStore>(relaxed = true)
        userDetails = mockk<UserDetails>(relaxed = true)
        error = mockk<DetailedServerError>(relaxed = true).apply {
            every { message }.returns(ERROR_MESSAGE)
        }
        networkHandler = mockk<INetworkHandler>(relaxed = true).apply {
            every { getMe(match { it == CORRECT_CREDENTIALS }) }.returns(
                Observable.just(userDetails)
            )
            every { getMe(match { it == INCORRECT_CREDENTIALS }) }.returns(Observable.error(error))
        }
        authTokenCreator = mockk<AuthTokenCreator>(relaxed = true)
        dataSerializer = mockk<DataSerializer>(relaxed = true)
        mockkStatic(Log::class)
        mockkStatic(Looper::class)
        every { Log.d(any(), any()) }.returns(0)
        every { Looper.getMainLooper() }.returns(mockk(relaxed = true))
    }

    @Test
    fun `Confirm delegate initializes with empty credential values and submit disable state`() =
        runTest {
            val delegate = LoginViewModelDelegate(
                credentialStore = credentialStore,
                userAccountStore = userAccountStore,
                iNetworkHandler = networkHandler,
                authTokenCreator = authTokenCreator,
                dataSerializer = dataSerializer,
            )
            credentialStore.setReturnValue(null)
            val viewmodel = LoginViewModel(delegate)
            val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            value1.assetViewState()
        }

    @Test
    fun `Confirm the submit login button is enabled only when minimum of 3 characters each are entered for username and password`() =
        runTest {
            val delegate = LoginViewModelDelegate(
                credentialStore = credentialStore,
                userAccountStore = userAccountStore,
                iNetworkHandler = networkHandler,
                authTokenCreator = authTokenCreator,
                dataSerializer = dataSerializer,
            )
            credentialStore.setReturnValue(null)
            val viewmodel = LoginViewModel(delegate)
            val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            value1.onPasswordChange("foobar")
            value1.onUsernameChange("no")
            val value2 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS).also {
                it.assetViewState(
                    expectedUserName = "no",
                    expectedPassword = "foobar",
                )
            }
            value2.onUsernameChange("noel")
            value2.onPasswordChange("fo")
            val value3 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS).also {
                it.assetViewState(
                    expectedUserName = "noel",
                    expectedPassword = "fo",
                )
            }
            value3.onUsernameChange("noe")
            value3.onPasswordChange("foo")
            viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS).also {
                it.assetViewState(
                    expectedUserName = "noe",
                    expectedPassword = "foo",
                    expectedCanLogin = true,
                )
            }
        }

    @Test
    fun `Entering correct username and password triggers list page`() = runTest {
        val delegate = LoginViewModelDelegate(
            credentialStore = credentialStore,
            userAccountStore = userAccountStore,
            iNetworkHandler = networkHandler,
            authTokenCreator = authTokenCreator,
            dataSerializer = dataSerializer,
        )
        credentialStore.setReturnValue(null)
        val viewmodel = LoginViewModel(delegate)
        val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
        value1.onPasswordChange("foobar")
        value1.onUsernameChange("noel")
        every {
            authTokenCreator.createAuthToken(
                any(),
                any()
            )
        }.returns(CORRECT_CREDENTIALS)
        every { userDetails.authToken }.returns(CORRECT_CREDENTIALS)
        value1.onLogin("noel", "foobar")
        viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS).also {
            it.assetViewState(
                expectedUserName = "noel",
                expectedPassword = "foobar",
                expectedCanLogin = true,
                expectedShowLoading = false,
                expectedTriggerListLaunch = true,
            )
            verify(exactly = 1) { userAccountStore.setUserId("noel") }
            verify(exactly = 1) { userAccountStore.setFirstName(any()) }
            verify(exactly = 1) { userAccountStore.setLastName(any()) }
            verify(exactly = 1) { userAccountStore.setTopDirectory(any()) }
            verify(exactly = 1) {
                credentialStore.setStoredCredentials(
                    FileBrowserCredentials(
                        CORRECT_CREDENTIALS
                    )
                )
            }
        }
    }

    @Test
    fun `Entering Incorrect username and password triggers error`() = runTest {
        val delegate = LoginViewModelDelegate(
            credentialStore = credentialStore,
            userAccountStore = userAccountStore,
            iNetworkHandler = networkHandler,
            authTokenCreator = authTokenCreator,
            dataSerializer = dataSerializer,
        )
        credentialStore.setReturnValue(null)
        val viewmodel = LoginViewModel(delegate)
        val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
        value1.onPasswordChange("foob")
        value1.onUsernameChange("noel")
        val value2 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
        every {
            authTokenCreator.createAuthToken(
                any(),
                any()
            )
        }.returns(INCORRECT_CREDENTIALS)

        value2.onLogin("noel", "foob")
        viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS).also {
            it.assetViewState(
                expectedUserName = "noel",
                expectedPassword = "foob",
                expectedCanLogin = true,
                expectedShowLoading = false,
                expectedTriggerListLaunch = false,
                expectedErrorMessage = ERROR_MESSAGE
            )
        }
    }

    private fun ViewState.assetViewState(
        expectedUserName: String = "",
        expectedPassword: String = "",
        expectedCanLogin: Boolean = false,
        expectedShowLoading: Boolean = false,
        expectedTriggerListLaunch: Boolean = false,
        expectedErrorMessage: String? = null,
    ) {
        Assert.assertEquals(expectedUserName, userName)
        Assert.assertEquals(expectedPassword, password)
        Assert.assertEquals(expectedCanLogin, canLogin)
        Assert.assertEquals(expectedErrorMessage, loginError?.errorMessage)
        Assert.assertEquals(expectedShowLoading, showLoading)
        Assert.assertEquals(expectedTriggerListLaunch, triggerListLaunch)

    }

    companion object {
        const val CORRECT_CREDENTIALS = "Basic bm9lbDpmb29iYXI="
        const val INCORRECT_CREDENTIALS = "incorrectAuthToken"
        const val ERROR_MESSAGE = "encountered error"
    }

}