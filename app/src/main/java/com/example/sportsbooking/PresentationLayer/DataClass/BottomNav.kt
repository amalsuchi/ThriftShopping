package com.example.sportsbooking.PresentationLayer.DataClass

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNav(
    val title:String,
    val selectedIcon:ImageVector,
    val unSelectedIcon:ImageVector,
    val hasNews : Boolean,
    val badgeCount :Int? = null
)
