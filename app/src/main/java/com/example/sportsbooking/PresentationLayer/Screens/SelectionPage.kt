package com.example.sportsbooking.PresentationLayer.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SelectionPage(navController: NavController){
    Column(modifier = Modifier.fillMaxSize()){
        Row(modifier = Modifier.fillMaxWidth()){
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
                .padding(10.dp)
                .background(Color.Gray)
                .weight(1f)){
                Text(text = "Shoes",Modifier.padding(top = 50.dp, bottom = 50.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
                .padding(10.dp)
                .background(Color.Gray)
                .weight(1f)){
                Text(text = "Shirts",Modifier.padding(top = 50.dp, bottom = 50.dp))
            }

        }
        Row(modifier = Modifier.fillMaxWidth()){
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
                .padding(10.dp)
                .background(Color.Gray)
                .weight(1f)){
                Text(text = "Shoes",Modifier.padding(top = 50.dp, bottom = 50.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
                .padding(10.dp)
                .background(Color.Gray)
                .weight(1f)){
                Text(text = "Shirts",Modifier.padding(top = 50.dp, bottom = 50.dp))
            }

        }

    }
}

@Composable
@Preview
fun preeview(){
  //  SelectionPage()
}