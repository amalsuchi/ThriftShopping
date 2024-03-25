package com.example.sportsbooking.PresentationLayer.Screens.WishlistScreen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sportsbooking.PresentationLayer.ViewModel.VM

@Composable
fun wishlistScreen(navController: NavController){
    val vm: VM = hiltViewModel()

    LaunchedEffect(Unit){
        vm.fetchWishlist()
    }

    val products by vm.products.collectAsState()

    LazyColumn(){
        items(products){ product ->
            ItemsinColumn(name = product.name!!, price = product.Price!!, image = product.imageUri?.get(0)!!.toUri())
        }
    }

}

@Composable
fun ItemsinColumn(name:String,price:Int,image: Uri){
    Row(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .padding(8.dp)
        .background(Color.LightGray),
        horizontalArrangement = Arrangement.SpaceEvenly){
        Column(modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))){
            AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .build(),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp)
                    .wrapContentHeight(),
                contentDescription ="" )
        }
        Column(){
            Text(text = name,modifier = Modifier.padding(4.dp) )
            Text(text ="$price",modifier = Modifier.padding(4.dp) )
        }

    }
}