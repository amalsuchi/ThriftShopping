package com.example.sportsbooking.PresentationLayer.Screens.SellClothesDetail


import android.net.Uri
import android.os.Build
import android.telecom.Call.Details

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.ImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import com.example.sportsbooking.PresentationLayer.Component.LoadingScreen

import com.example.sportsbooking.PresentationLayer.ViewModel.VM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun sellMensFashion(navController: NavController){

    val viewmodel:VM = hiltViewModel()
    val uiState = viewmodel.uiState.value
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    var imageUriList = remember{ mutableStateOf<List<Uri>>(emptyList()) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uriList ->
            imageUriList.value = uriList
        }
    )
    LaunchedEffect(Unit) {
        viewmodel.getUserData()

    }
    when {
        uiState.isLoading -> LoadingScreen()
        uiState.isSuccess && uiState.user != null -> {
            val userName =uiState.user.name ?: "error"
            val location = uiState.user.normallocation ?: "error"
            Column(modifier = Modifier
                .padding(4.dp)
                .verticalScroll(scrollState)
                .fillMaxSize())
            {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)){

                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = 4.dp,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        content = {
                            items(imageUriList.value.size) { uri ->
                                AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUriList.value[uri])
                                    .build(),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight(),
                                    contentDescription ="" )
                            }
                        },

                    )

                }


                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                    Button(modifier = Modifier.padding(4.dp),
                        onClick = {
                                galleryLauncher.launch("image/*")
                        }
                    ) {
                        Text(
                            text = "Pick images"
                        )
                    }
                }

                var itemName by remember{ mutableStateOf("") }
                var Size by remember{ mutableStateOf("") }
                var details by remember{ mutableStateOf("") }
                var price by remember{ mutableStateOf("") }

                Column(Modifier.fillMaxWidth()){
                    OutlinedTextField(value = itemName , onValueChange ={
                            newtext -> itemName=newtext
                    },
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        placeholder = { Text(text = "Item Name")}
                    )
                    OutlinedTextField(value = details , onValueChange ={
                            detailss -> details=detailss
                    },
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        placeholder = { Text(text = "Add Details")}
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                        OutlinedTextField(value = Size , onValueChange ={
                                sizes -> Size=sizes
                        },
                            modifier = Modifier
                                .padding(4.dp),
                            placeholder = { Text(text = "Size")}
                        )
                        OutlinedTextField(value = price , onValueChange ={
                                newprice -> price=newprice
                        },
                            modifier = Modifier
                                .padding(4.dp),
                            placeholder = { Text(text = "Price")},
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Button(onClick = {
                        coroutineScope.launch {

                            viewmodel.uploadFashionItemsdetail("Men",userName,itemName,location,Size,details,price.toInt(),imageUriList.value)
                        }
                    }
                    )
                    {
                        if (uiState.isLoading) {
                            CircularProgressIndicator()
                        } else {
                            Text(text = "Save")
                        }
                    }
                }

            }
        }
        uiState.isSuccess.not() ->{
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Error loading data")

            }

        }
    }


}