package com.example.sportsbooking.PresentationLayer.Screens.UserAdd

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sportsbooking.PresentationLayer.Component.LoadingScreen
import com.example.sportsbooking.PresentationLayer.ViewModel.VM
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourAdds(navController: NavController){
    val vm : VM = hiltViewModel()
    val uiState = vm.uiState.value
    val scrollState = rememberScrollState()

   // val aa = listOf("AA","BB")
    LaunchedEffect(Unit){
        vm.ShowUserAdds()
    }
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate("Sell") }) {
            Icon(Icons.Default.Add,null)
        }
    },
        floatingActionButtonPosition = FabPosition.End,
        content = {
            when{
                uiState.isLoading -> LoadingScreen()
                uiState.isSuccess -> UserAddsList(userAdds = uiState.userAdds, navController = navController)
                !uiState.isSuccess ->{
                    Log.d("dang", "dagnabit")
                }
            }
        }
    )
}


@Composable
fun UserAddsList(userAdds: List<DocumentSnapshot>?, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize()
    ) {
        items(userAdds ?: emptyList()) { documentSnapshot ->
            val name = documentSnapshot.getString("name") ?: ""
            val price = documentSnapshot.getLong("Price")?.toInt() ?: 0
            val id = documentSnapshot.getString("id") ?: ""
            val imageList = documentSnapshot.get("imageUri") as? List<String> ?: emptyList()
            val uriList: List<Uri> = imageList.map { Uri.parse(it) }

            UserAddItem(name, price, id, uriList, navController)
        }
    }
}

@Composable
fun UserAddItem(name: String, price: Int, id: String, uriList: List<Uri>, navController: NavController) {
    val imageName = uriList[0]

    Column(
        modifier = Modifier
            .padding(4.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(50.dp))
            .clickable {

                if (id.isNotEmpty()) {
                    Log.d("gimmietheid", id)
                    navController.navigate("UserAddDetail/$id")
                }
            }
            .background(Color.LightGray)
            .fillMaxWidth()
    ) {

        Row {
            Column(modifier = Modifier.width(180.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageName)
                        .build(),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .wrapContentHeight(),
                    contentDescription = ""
                )
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Item Name:", modifier = Modifier.padding(4.dp))
                    Text(text = name, modifier = Modifier.padding(4.dp))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Price:", modifier = Modifier.padding(4.dp))
                    Text(text = "$price", modifier = Modifier.padding(4.dp))
                }
            }

        }
    }
}
@Composable
fun fuckingImage(imageUri:Uri){

    Log.d("urivaalue","$imageUri")
}
