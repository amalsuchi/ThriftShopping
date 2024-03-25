package com.example.sportsbooking.PresentationLayer.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.AddBusiness
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sportsbooking.PresentationLayer.DataClass.BottomNav
import com.example.sportsbooking.PresentationLayer.Screens.Chat.chatPage
import com.example.sportsbooking.PresentationLayer.Screens.HomeScreen.newHomeScreen
import com.example.sportsbooking.PresentationLayer.Screens.SellClothesDetail.sellMensFashion
import com.example.sportsbooking.PresentationLayer.Screens.SellScreen.sellart
import com.example.sportsbooking.PresentationLayer.Screens.SellScreen.sellbooks
import com.example.sportsbooking.PresentationLayer.Screens.SellScreen.sellclothes

import com.example.sportsbooking.PresentationLayer.Screens.SellScreen.sellelectronics
import com.example.sportsbooking.PresentationLayer.Screens.SellScreen.sellkitchenware
import com.example.sportsbooking.PresentationLayer.Screens.SellScreen.sellsports
import com.example.sportsbooking.PresentationLayer.Screens.SellScreen.selltoys
import com.example.sportsbooking.PresentationLayer.Screens.UserAdd.YourAddDetails
import com.example.sportsbooking.PresentationLayer.Screens.UserAdd.YourAdds
import com.example.sportsbooking.PresentationLayer.Screens.WishlistScreen.wishlistScreen


@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun mainPageScreen(navController: NavController){

    val bottomBar = listOf(
        BottomNav(
            title = "Profile",
            selectedIcon = Icons.Default.Person,
            unSelectedIcon = Icons.Outlined.Person,
            hasNews = false,
            badgeCount = null
        ),
        BottomNav(
            title = "Add",
            selectedIcon = Icons.Default.AddBusiness,
            unSelectedIcon = Icons.Outlined.AddBusiness,
            hasNews = false,
            badgeCount = null
        ),
        BottomNav(
            title = "Home",
            selectedIcon = Icons.Default.Home,
            unSelectedIcon = Icons.Outlined.Home,
            hasNews = false,
            badgeCount = null
        ),
        BottomNav(
            title = "Wishlist",
            selectedIcon = Icons.Default.ShoppingBag,
            unSelectedIcon = Icons.Outlined.ShoppingBag,
            hasNews = false,
            badgeCount = null
        ),
        BottomNav(
            title = "Chat",
            selectedIcon = Icons.Default.Chat,
            unSelectedIcon = Icons.Outlined.Chat,
            hasNews = false,
            badgeCount = null
        ),



    )
    var selectedItemIndex by rememberSaveable{ mutableStateOf(0) }
    val navController = rememberNavController()

    Surface( color = MaterialTheme.colorScheme.background){
        Scaffold(
            modifier = Modifier
            .fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    bottomBar.forEachIndexed { index, bottomNav ->  
                        NavigationBarItem(
                            selected = selectedItemIndex ==index, 
                            onClick = { selectedItemIndex = index
                                      navController.navigate(bottomNav.title)
                                      },
                            label = {
                                    Text(text = bottomNav.title)
                            },
                            icon = { 
                                BadgedBox(
                                    badge = {
                                        if(bottomNav.badgeCount != null){
                                            Badge {
                                                Text(text = bottomNav.badgeCount.toString())
                                            }
                                        }
                                        else if(bottomNav.hasNews){
                                            Badge {

                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if(index == selectedItemIndex){
                                            bottomNav.selectedIcon
                                        }else{
                                            bottomNav.unSelectedIcon
                                        },
                                        contentDescription = bottomNav.title
                                    )

                                }
                            })
                    }
                }
            }
        ){
            innerpadding ->
            Box(modifier = Modifier
                .padding(innerpadding)
                .fillMaxSize()){
                NavHost(navController = navController , startDestination ="Profile" ){
                    composable("Profile"){
                        profileScreen(navController = navController)
                    }
                    composable("Home"){
                        newHomeScreen(navController = navController)
                    }
                    composable("Add"){
                        YourAdds(navController = navController)
                    }
                    composable("ProfileEditScreen"){
                        profileEditScreen(navController = navController)
                    }
                    composable("Sell"){
                        SellProduct(navController = navController)
                    }
                    composable("sellclothes"){
                        sellclothes(navController = navController)
                    }
                    composable("sellart"){
                        sellart(navController = navController)
                    }
                    composable("sellfurniture"){
                        sellart(navController = navController)
                    }
                    composable("sellkitchenware"){
                        sellkitchenware(navController = navController)
                    }
                    composable("sellbooks"){
                        sellbooks(navController = navController)
                    }
                    composable("sellsports"){
                        sellsports(navController = navController)
                    }
                    composable("selltoys"){
                        selltoys(navController = navController)
                    }
                    composable("sellelectronics"){
                        sellelectronics(navController = navController)
                    }
                    composable("SellMensFashion"){
                        sellMensFashion(navController = navController)
                    }
                    composable("Wishlist"){
                        wishlistScreen(navController = navController)
                    }
                    composable("Chat"){
                        chatPage(navController = navController)
                    }
                    composable("UserAddDetail/{id}"){ backStackEntry ->
                        backStackEntry.arguments?.getString("id")
                            ?.let { YourAddDetails(navController = navController, id = it) }
                    }

                    composable("EditImage/{id}"){ backStackEntry ->
                        backStackEntry.arguments?.getString("id")
                            ?.let { editImage(navController = navController,id = it) }
                    }


                }
            }
        }
    }

}