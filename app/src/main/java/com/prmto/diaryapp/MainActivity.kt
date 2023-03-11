package com.prmto.diaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.prmto.diaryapp.navigation.Screen
import com.prmto.diaryapp.navigation.SetupNavGraph
import com.prmto.diaryapp.ui.theme.DiaryAppTheme
import com.prmto.diaryapp.util.Constants.APP_ID
import io.realm.kotlin.mongodb.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            DiaryAppTheme {
               val navController = rememberNavController()

                SetupNavGraph(
                    startDestinationScreen = getStartDestination(),
                    navController = navController,
                )
            }
        }
    }
}

private fun getStartDestination():Screen{
    val user = App.create(APP_ID).currentUser
    return if (user !=null && user.loggedIn) Screen.Home else Screen.Authentication
}