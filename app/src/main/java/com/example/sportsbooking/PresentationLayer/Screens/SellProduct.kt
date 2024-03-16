package com.example.sportsbooking.PresentationLayer.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SellProduct(navController: NavController) {
    val items = listOf("Art and Decor" to "sellart", "Furniture" to "sellfurniture",
        "Kitchenware" to "sellkitchenware", "Books and Posters" to "sellbooks",
        "Clothing and Accessories" to "sellclothes",
        "Sports and Outdoor Equipment" to "sellsports",
        "Toys and Board Games" to "selltoys","Electronics" to "sellelectronics")

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .padding(10.dp)
            .fillMaxWidth()
        ) {
            Text(
                text = "Whatchu looking to sell?", // Consider capitalization and punctuation for clarity
                modifier = Modifier.padding(10.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(125.dp), // Adjust number of columns based on preference
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 16.dp,
                end = 12.dp,
                bottom = 16.dp
            ),
            content = {
                items(items.size) { index ->
                    val item = items[index] // Access item using index for clarity

                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                            .height(150.dp)
                            .clickable {
                                // Adjust navigation logic based on your requirements
                                navController.navigate("${item.second}") // Option 1: Navigate to a specific screen
                                // navController.navigate("productDetails?item=$item") // Option 2: Pass item data
                            },

                      //  elevation = CardDefaults.cardElevation, // Use Material Design defaults
                    ) {
                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                            Text(
                                text = item.first,
                                textAlign = TextAlign.Center,
                                fontSize = 25.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}
