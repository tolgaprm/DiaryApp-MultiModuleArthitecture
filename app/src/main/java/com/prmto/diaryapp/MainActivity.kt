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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            DiaryAppTheme {
               val navController = rememberNavController()

                SetupNavGraph(
                    startDestinationScreen = Screen.Authentication,
                    navController = navController,
                )
            }
        }
    }
}