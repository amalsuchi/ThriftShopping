package com.example.sportsbooking.PresentationLayer.Screens.SellScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.navigation.NavController
import kotlin.time.Duration.Companion.seconds

@Composable
fun sellclothes(navController: NavController) {
    val categories = listOf(
        "Men" to "SellMensFashion",
        "Women" to "SellWomensFashion",
        "Unisex" to "SellUnisexFashion"
    )

    LazyColumn {
        items(categories) { (title, route) -> // Destructuring on the fly
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable {
                        navController.navigate(route)
                    },
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .fillMaxWidth() // Redundant, use only one
                            .align(Alignment.CenterHorizontally),
                      //  verticalArrangement = Arrangement.Center// Combined horizontal and vertical alignment
                    ) {
                        Text(
                            text = title,
                            textAlign = TextAlign.Center,
                            fontSize = 25.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            )
        }
    }
}