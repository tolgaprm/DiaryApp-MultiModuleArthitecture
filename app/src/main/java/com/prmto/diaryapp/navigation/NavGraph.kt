package com.prmto.diaryapp.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.prmto.diaryapp.presantation.screens.auth.AuthenticationScreen
import com.prmto.diaryapp.util.Constants
import com.prmto.diaryapp.util.Constants.WRITE_SCREEN_ARGUMENT_KEY

@Composable
fun SetupNavGraph(startDestinationScreen:Screen, navController: NavHostController) {

    NavHost(navController = navController, startDestination = startDestinationScreen.route ){
        authenticationRoute()
        homeRoute()
        writeRoute()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun NavGraphBuilder.authenticationRoute(){
    composable(Screen.Authentication.route){
        AuthenticationScreen(
            loadingState = false,
            onButtonClicked = {

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