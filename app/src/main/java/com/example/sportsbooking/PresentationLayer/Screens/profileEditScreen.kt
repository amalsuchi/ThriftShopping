package com.example.sportsbooking.PresentationLayer.Screens


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sportsbooking.PresentationLayer.ViewModel.VM
import com.example.sportsbooking.PresentationLayer.signIn.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profileEditScreen(navController: NavController){

    val vm:VM = hiltViewModel()
    val auth = Firebase.auth
    val email = auth.currentUser!!.email.toString()
    var name by remember { mutableStateOf("") }


    Column(modifier = Modifier
        .padding()
        .fillMaxSize()){
        OutlinedTextField(
            value = name,
            onValueChange ={name = it},
            label = { Text(text = "name")},
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        )


        Button(onClick = {
            if(!name.isNullOrEmpty()){
                vm.createOrUpdateDocument(name,email) {
                    navController.navigate("Profile")
                }
            }
        }){
            Text(text = "Save")
        }
    }


}