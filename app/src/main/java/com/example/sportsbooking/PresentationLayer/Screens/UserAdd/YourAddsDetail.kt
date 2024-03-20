package com.example.sportsbooking.PresentationLayer.Screens.UserAdd

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sportsbooking.PresentationLayer.Component.ImageSlider
import com.example.sportsbooking.PresentationLayer.Component.LoadingScreen
import com.example.sportsbooking.PresentationLayer.ViewModel.VM
import com.google.accompanist.pager.ExperimentalPagerApi

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun YourAddDetails(navController: NavController,id:String){

    val vm: VM = hiltViewModel()
    val uiState = vm.uiState.value

    val scrollState = rememberScrollState()

    var updateDataButtonClicked by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit){
        Log.d("iddl", id)
        vm.ShowDetailedUserAdds(id)
    }

    when{
        uiState.isLoading -> LoadingScreen()
        uiState.isSuccess -> {

            var imagelist = uiState.FashionUserProductData?.imageUri
            var urilist: List<Uri> = imagelist!!.map { Uri.parse(it) }


            val location = uiState.location ?:"error"

            Column(modifier = Modifier
                .padding(4.dp)
                .verticalScroll(scrollState)
                .fillMaxSize())
            {

                if (urilist != null) {
                    ImageSlider(imageList = urilist)
                }
                
                Column(modifier = Modifier.fillMaxWidth()){
                    Button(onClick = { navController.navigate("EditImage/$id") }) {
                        Text(text = "Edit Images")
                    }
                }

                var itemName by remember{ mutableStateOf(uiState.FashionUserProductData?.name.toString() ?: "") }
                var Size by remember{ mutableStateOf(uiState.FashionUserProductData?.Size.toString()) }
                var details by remember{ mutableStateOf(uiState.FashionUserProductData?.Detail.toString()) }
                var price by remember{ mutableStateOf(uiState.FashionUserProductData?.Price.toString()) }

                Column(Modifier.fillMaxWidth()){
                    OutlinedTextField(
                        value = itemName,
                        onValueChange ={ newtext ->
                            itemName=newtext },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        label = { Text(text = "Item")},
                        placeholder = { Text(text = "Item Name") }
                    )

                    OutlinedTextField(
                        value = details ,
                        onValueChange ={detailss -> details=detailss},
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        label = { Text(text = "Detail")},
                        placeholder = { Text(text = "Add Details") }
                    )
                    OutlinedTextField(value = Size , onValueChange ={
                            sizes -> Size=sizes
                    },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        placeholder = { Text(text = "Size") },
                        label = { Text(text = "Size")}
                    )

                    OutlinedTextField(value = price , onValueChange ={ newprice
                        -> price=newprice
                    },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        placeholder = { Text(text = "Price") },
                        label = { Text(text = "Price")},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                   Button(onClick = {
                       updateDataButtonClicked = true
                   })
                   {
                       if (uiState.isLoading) {
                           CircularProgressIndicator()
                       } else {
                           Text(text = "Update")
                       }
                   }

                    LaunchedEffect(updateDataButtonClicked) {
                        if(updateDataButtonClicked ){
                            vm.updateFashionItemsdetail(id,itemName,location,Size,details,price.toInt())
                            navController.navigate("Add")

                        }
                    }

                }

            }
        }
        !uiState.isSuccess ->{
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                Text(text = "There was an error loading data")
                Button(onClick = { navController.navigate("Add") }) {
                    Text(text = "Back")
                }
            }
        }
    }
}


