package com.prmto.diaryapp.navigation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.prmto.diaryapp.R
import com.prmto.diaryapp.presantation.screens.auth.AuthenticationScreen
import com.prmto.diaryapp.presantation.screens.auth.AuthenticationViewModel
import com.prmto.diaryapp.util.Constants.APP_ID
import com.prmto.diaryapp.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SetupNavGraph(startDestinationScreen: Screen, navController: NavHostController) {

    val context = LocalContext.current

    NavHost(navController = navController, startDestination = startDestinationScreen.route) {
        authenticationRoute(
            context,
            onNavigateHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            }
        )

        homeRoute()
        writeRoute()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun NavGraphBuilder.authenticationRoute(
    context: Context,
    onNavigateHome: () -> Unit,
) {
    composable(Screen.Authentication.route) {
        val viewModel: AuthenticationViewModel = viewModel()
        val authenticated = viewModel.authenticatedState.value
        val loadingState = viewModel.loadingState.value
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        AuthenticationScreen(
            authenticated = authenticated,
            loadingState = loadingState,
            oneTapSignInState = oneTapState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onTokenIdReceived = { tokenId ->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess(context.getString(R.string.successfully_authenticated))
                        viewModel.setLoading(false)
                    },
                    onError = {
                        messageBarState.addError(it)
                        viewModel.setLoading(false)
                    }
                )
            },
            onDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            onNavigateToHome =  onNavigateHome
        )
    }
}

fun NavGraphBuilder.homeRoute() {
    composable(Screen.Home.route) {
        val scope = rememberCoroutineScope()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Button(onClick = {
                scope.launch(Dispatchers.IO){
                    App.create(APP_ID).currentUser?.logOut()
                }
            }) {
                Text(text = "Logout")
            }
        }
    }
}

fun NavGraphBuilder.writeRoute() {
    composable(Screen.Write.route,
        arguments = listOf(
            navArgument(
                name = WRITE_SCREEN_ARGUMENT_KEY,
            ) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) {

    }
}