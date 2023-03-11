package com.prmto.diaryapp.navigation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.prmto.diaryapp.R
import com.prmto.diaryapp.presantation.components.DisplayAlertDialog
import com.prmto.diaryapp.presantation.screens.auth.AuthenticationScreen
import com.prmto.diaryapp.presantation.screens.auth.AuthenticationViewModel
import com.prmto.diaryapp.presantation.screens.home.HomeScreen
import com.prmto.diaryapp.util.Constants.APP_ID
import com.prmto.diaryapp.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        homeRoute(
            onNavigateToWrite = {
                navController.navigate(Screen.Write.route)
            },
            onNavigateToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            }
        )
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
            onNavigateToHome = onNavigateHome
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.homeRoute(
    onNavigateToWrite: () -> Unit,
    onNavigateToAuth: () -> Unit,
) {
    composable(Screen.Home.route) {
        val scope = rememberCoroutineScope()
        val signOutDialogOpened = remember { mutableStateOf(false) }
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        HomeScreen(
            drawerState = drawerState,
            onNavigateToWrite = onNavigateToWrite,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            },
            onSignOutClick = {
                signOutDialogOpened.value = true
            }
        )


        DisplayAlertDialog(
            title = stringResource(id = R.string.sign_out),
            message = stringResource(id = R.string.alert_sign_out),
            dialogOpened = signOutDialogOpened.value,
            onDialogClosed = { signOutDialogOpened.value = false },
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(APP_ID).currentUser
                    if (user != null) {
                        user.logOut()
                        withContext(Dispatchers.Main){
                            onNavigateToAuth()
                        }
                    }
                }
            }
        )
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