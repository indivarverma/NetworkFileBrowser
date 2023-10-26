package com.indivar.filebrowser.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indivar.filebrowser.R
import com.indivar.filebrowser.common.core.login.LoginScreenCoordinator
import com.indivar.filebrowser.common.core.login.LoginViewModel
import com.indivar.filebrowser.common.core.login.ViewState
import com.indivar.filebrowser.injectScoped
import com.indivar.filebrowser.listing.composables.ErrorDialog
import com.indivar.filebrowser.ui.theme.NetworkFileBrowserTheme
import org.koin.core.parameter.parametersOf

class LoginScreen : ComponentActivity() {
    private val viewModel: LoginViewModel by injectScoped { parametersOf(this) }
    private val coordinator: LoginScreenCoordinator by injectScoped { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelStore
        viewModel.state.observe(this) { it.render() }
    }

    private fun ViewState.render() {
        setContent {
            if (this.triggerListLaunch) {
                finish()
                coordinator.onSuccessfulLogin()
            } else {
                NetworkFileBrowserTheme {
                    LoginScreenView(this)
                }
            }
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, LoginScreen::class.java)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreenView(state: ViewState) {
    var username by remember {
        mutableStateOf(state.userName)
    }
    var password by remember {
        mutableStateOf(state.password)
    }
    var showPassword by remember {
        mutableStateOf(false)
    }
    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(text = stringResource(id = R.string.login)) }, actions = {
            when {
                state.showLoading -> CircularProgressIndicator()
                state.canLogin -> {
                    IconButton(onClick = { state.onLogin.invoke(username, password) }) {
                        Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }

        })
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 36.dp, vertical = 24.dp)
        ) {
            Column {
                TextField(
                    value = username,
                    label = {
                        Text(text = stringResource(id = R.string.username))
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    onValueChange = {
                        username = it
                        state.onUsernameChange(it)
                    },


                    )
                TextField(
                    value = password,
                    label = {
                        Text(text = stringResource(id = R.string.password))
                    },
                    trailingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.password_eye),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    showPassword = !showPassword
                                },
                        )
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Go,
                    ),
                    keyboardActions = KeyboardActions(onGo = {
                        state.onLogin.invoke(username, password)
                    }),
                    onValueChange = {
                        password = it
                        state.onPasswordChange(it)
                    },

                    )
            }
        }
    }
    state.loginError?.let {
        ErrorDialog(it)
    }
}

@Preview
@Composable
private fun LoginScreenViewPreview() {
    LoginScreenView(
        state = ViewState(
            userName = "someusername",
            password = "password",
            showLoading = false,
            canLogin = true,
            loginError = null,
            triggerListLaunch = false,
            onLogin = { _, _ -> },
            onUsernameChange = {},
            onPasswordChange = {},
        )
    )
}