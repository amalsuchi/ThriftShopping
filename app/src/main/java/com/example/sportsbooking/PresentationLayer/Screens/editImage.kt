package com.example.sportsbooking.PresentationLayer.Screens


import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sportsbooking.PresentationLayer.Component.LoadingScreen
import com.example.sportsbooking.PresentationLayer.ViewModel.VM
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

@Composable
fun editImage(navController: NavController,id:String){

    val vm: VM = hiltViewModel()
    val uiState = vm.uiState.value

    var showImages = remember {
        mutableStateListOf<Boolean>()
    }
    var imagesToDeleteFromStorage = remember {
        mutableListOf<String>()
    }
    var galleryImageUriList = remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    var buttoncliked = remember {
        mutableStateOf(false)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uri ->
            galleryImageUriList.value = uri
        }
    )
    val scrollState = rememberScrollState()
    LaunchedEffect(id){
        vm.ShowDetailedUserAdds(id)
    }
    when{
        uiState.isLoading -> LoadingScreen()
        uiState.isSuccess ->{
            var imagelist = uiState.FashionUserProductData?.imageUri

            var imageUri = imagelist!!.map { Uri.parse(it) }


            Box(modifier = Modifier.height(1000.dp).verticalScroll(rememberScrollState())) {
                Column() {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .height(500.dp)
                            .background(Color.LightGray)
                            .clip(
                                RoundedCornerShape(40.dp)
                            )
                    ) {
                        Text(
                            text = "New Images to Add",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(4.dp)
                        )
                        {
                            items(imageUri.size) { uri ->
                                Log.d("imageuri", "$imageUri")
                                if (showImages[uri]) {
                                    Box(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .clip(
                                                RoundedCornerShape(30.dp)
                                            )
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(imageUri[uri])
                                                .build(),
                                            contentScale = ContentScale.FillHeight,
                                            modifier = Modifier
                                                .size(200.dp)
                                                .wrapContentHeight(),
                                            contentDescription = ""
                                        )
                                        SmallFloatingActionButton(
                                            onClick = {
                                                imagesToDeleteFromStorage.add(imagelist!![uri])
                                                showImages[uri] = false
                                                Log.d("button", "$showImages")

                                            },
                                            modifier = Modifier.align(Alignment.TopEnd)
                                        ) {
                                            Icon(Icons.Filled.Close, contentDescription = "Add")
                                        }
                                    }
                                }

                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .height(500.dp)
                            .background(Color.LightGray)
                            .clip(RoundedCornerShape(50.dp))
                    ) {
                        Text(
                            text = "New Images to Add",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            items(galleryImageUriList.value.size) { uri ->
                                Log.d("imageuri", "$imageUri")
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clip(
                                            RoundedCornerShape(30.dp)
                                        )
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(galleryImageUriList.value[uri])
                                            .build(),
                                        contentScale = ContentScale.FillHeight,
                                        modifier = Modifier
                                            .size(200.dp)
                                            .wrapContentHeight(),
                                        contentDescription = ""
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .background(Color.LightGray)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            galleryLauncher.launch("image/*")
                        }) {
                            Text(text = "Add Image")
                        }
                        Button(onClick = {
                            buttoncliked.value = true
                            Log.d("clicked","$buttoncliked")
                        }) {
                            Text(text = "Save")
                        }
                    }

                }
            }

            Log.d("clicked","$buttoncliked")
            LaunchedEffect(buttoncliked.value){
                Log.d("clicked","$buttoncliked")
                if(buttoncliked.value){
                    vm.deleteImage(imagesToDeleteFromStorage,id)
                    if(galleryImageUriList.value.isNotEmpty()){
                        vm.uploadImages(id,galleryImageUriList.value)
                    }
                }

            }
            when{
               //uiState.isLoading -> LoadingScreen()

            }


        }
    }
}