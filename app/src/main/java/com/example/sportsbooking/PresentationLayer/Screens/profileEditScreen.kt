package com.example.sportsbooking.PresentationLayer.Screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sportsbooking.PresentationLayer.ViewModel.VM
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profileEditScreen(navController: NavController){

    val vm:VM = hiltViewModel()
    val auth = Firebase.auth
    val email = auth.currentUser?.email.toString()
    var name = remember{ mutableStateOf("") }
  //  var district = remember { mutableStateOf("")}
    //val email =
    var firstTime = true

    Column(modifier = Modifier
        .padding()
        .fillMaxSize()){
        OutlinedTextField(value = name.value,
            onValueChange ={name.value = it},
            label = { Text(text = "name")},
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        )


        Button(onClick = {if(firstTime){
            vm.UpdateUserData(name.value, email)
            firstTime = false
        }else{
            vm.UpdateUserData(name.value,email)

        }
            navController.navigate("Profile")


        }) {
            Text(text = "Save")
        }
    }


}