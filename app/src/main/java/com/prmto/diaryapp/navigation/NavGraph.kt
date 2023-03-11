package com.prmto.diaryapp.navigation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
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
import com.prmto.diaryapp.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState

@Composable
fun SetupNavGraph(startDestinationScreen:Screen, navController: NavHostController) {

    val context = LocalContext.current

    NavHost(navController = navController, startDestination = startDestinationScreen.route ){
        authenticationRoute(context)
        homeRoute()
        writeRoute()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun NavGraphBuilder.authenticationRoute(
    context:Context
){
    composable(Screen.Authentication.route){
        val viewModel:AuthenticationViewModel= viewModel()
        val loadingState = viewModel.loadingState.value
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        AuthenticationScreen(
            loadingState = loadingState,
            oneTapSignInState = oneTapState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onTokenIdReceived = {tokenId->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                       if (it){
                           messageBarState.addSuccess(context.getString(R.string.successfully_authenticated))
                           viewModel.setLoading(false)
                       }
                    },
                    onError = {
                        messageBarState.addError(it)
                        viewModel.setLoading(false)
                    }
                )
            },
            onDialogDismissed = {message->
                messageBarState.addError(Exception(message))
            }
        )
    }
}

fun NavGraphBuilder.homeRoute(){
    composable(Screen.Home.route){


    }
}

fun NavGraphBuilder.writeRoute(){
    composable(Screen.Write.route,
        arguments = listOf(
            navArgument(
                name = WRITE_SCREEN_ARGUMENT_KEY,
            ){
                type = NavType.StringType
                nullable=true
                defaultValue=null
            }
        )
    ){

    }
}