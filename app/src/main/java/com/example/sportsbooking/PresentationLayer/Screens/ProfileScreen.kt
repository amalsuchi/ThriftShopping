package com.example.sportsbooking.PresentationLayer.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sportsbooking.PresentationLayer.DataClass.UiResponse
import com.example.sportsbooking.PresentationLayer.ViewModel.VM
import com.example.sportsbooking.PresentationLayer.signIn.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun profileScreen(navController: NavController) {
    val vm: VM = hiltViewModel()

    val coroutineScope = rememberCoroutineScope()
    val uiResponse = vm.uiResponse.value

    val context = LocalContext.current
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context =  context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    LaunchedEffect(Unit) {
        vm.getUserData()

    }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)){
        when {
            uiResponse.isLoading -> CircularProgressIndicator()
            uiResponse.isSuccess && uiResponse.userInfoData != null -> {
                textfieldrow(uiResponse = uiResponse.userInfoData.name)
                textfieldrow(uiResponse = uiResponse.userInfoData.email)
                textfieldrow(uiResponse = uiResponse.userInfoData.approxGeolocation)
            }
            uiResponse.isSuccess.not() ->{
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Please enter user Data")
                }

                Button(onClick = { navController.navigate("ProfileEditScreen") }, modifier = Modifier.padding(10.dp)) {
                    Text(text = "Add User Info")
                }

            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier
                .padding(10.dp)
                .background(Color.LightGray)
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .fillMaxWidth()){



                Column(modifier = Modifier.padding(10.dp)){
                    Button(onClick = { navController.navigate("ProfileEditScreen") }) {
                        Text(text = "Edit")
                    }
                }

                Column(modifier = Modifier.padding(10.dp)){
                    Button(onClick = {
                        coroutineScope.launch {
                            googleAuthUiClient.signOut()
                            navController.navigate("Auth")
                        }

                    }){
                        Text(text = "LogOut")
                    }
                }
            }
        }
    }


}

@Composable
fun textfieldrow(uiResponse:String?){
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Name:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        if(uiResponse?.isEmpty() == true){
            Text(text = "Please Enter Your Name before moving forward", modifier = Modifier.background(Color.Red))
        }
        Text(text = "${uiResponse}")

    }
}

