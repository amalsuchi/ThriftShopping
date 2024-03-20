package com.example.sportsbooking.PresentationLayer.Screens.HomeScreen

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

import com.example.sportsbooking.PresentationLayer.Component.imageSlider2
import com.example.sportsbooking.PresentationLayer.DataClass.ProductDataFlow

import com.example.sportsbooking.PresentationLayer.ViewModel.VM
import com.haroncode.lazycardstack.LazyCardStack
import com.haroncode.lazycardstack.items
import com.haroncode.lazycardstack.rememberLazyCardStackState

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun HomeScreen(navController: NavController){
    val vm : VM = hiltViewModel()
    LaunchedEffect(Unit){
        vm.fetchProducts()
    }
    val products by vm.products.collectAsState()
    Log.d("listdataaa","${products.size}")




    val cardStackState= rememberLazyCardStackState()
   if(products.isNotEmpty()){
       LazyCardStack(
           onSwipedItem = { index, direction ->
               if("$direction"=="Right"){
                   vm.addItemToLikedProducts(products[index].id!!)
                   Log.d("listdata", products[index].id.toString())
               }

           },
           state = cardStackState){
           items(products,{it.hashCode()}){
               UserAddItem(item = it)


           }
       }
   }





}



@Composable
fun UserAddItem(item:ProductDataFlow) {
    val name = item.name
    val size =  item.Size
    val location = item.Location
    val detail = item.Detail
    val price = item.Price.toString()
    val id = item.id
    val imageList =item.imageUri
    val urilist = imageList!!.map { Uri.parse(it) }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.LightGray)
            .clickable {

            }
            .fillMaxSize()
    ) {

        if(urilist != null){
            imageSlider2(list = urilist)
        }


        Column(modifier = Modifier.fillMaxWidth(),) {
            detailedRow(title = "Item name", content = name!!)
            detailedRow(title = "Detail", content = detail!!)
            detailedRow(title = "Size", content = size!!)
            detailedRow(title = "Price", content = price)
            detailedRow(title = "Location", content = location!!)
        }
    }
}
@Composable
fun detailedRow(title:String,content:String){
    Row(modifier = Modifier.fillMaxWidth()){
        Row(modifier = Modifier
            .padding(top = 8.dp, start = 8.dp, bottom = 8.dp)
            .width(100.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = title, modifier = Modifier.padding(4.dp),fontWeight = FontWeight.Bold)
            Text(text = ":", modifier = Modifier.padding(4.dp),fontWeight = FontWeight.Bold)

        }
        Row(modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
            .fillMaxWidth()) {
            Text(text = content, modifier = Modifier.padding(4.dp))
        }
    }

}


