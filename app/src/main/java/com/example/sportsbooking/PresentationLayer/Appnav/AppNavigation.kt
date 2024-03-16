package com.example.sportsbooking.PresentationLayer.Appnav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sportsbooking.PresentationLayer.Screens.Permission

import com.example.sportsbooking.PresentationLayer.Screens.authScreen
import com.example.sportsbooking.PresentationLayer.Screens.mainPageScreen


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun appnavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController , startDestination = "Auth"  ){
        composable("Auth"){
            authScreen(navController)
        }
        composable("PermissionPage"){
            Permission(navController = navController)
        }
        composable("MainPage"){
            mainPageScreen(navController = navController)
        }
    }
}