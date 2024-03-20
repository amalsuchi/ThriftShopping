package com.example.sportsbooking.PresentationLayer.Screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sportsbooking.PresentationLayer.ViewModel.VM
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.io.IOException
import java.util.Locale


//private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

@Composable
fun Permission(navController: NavController){
    val context = LocalContext.current
    var latitude  by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }

    var locationText by remember { mutableStateOf("") }
    var showPermissionResultText by remember { mutableStateOf(false) }
    var permissionResultText by remember { mutableStateOf("Permission Granted...") }

    var locationSettingsChecked by remember { mutableStateOf(false) }
    var locationSettingsRequested by remember { mutableStateOf(false) }
    val auth = Firebase.auth
    val email = auth.currentUser?.email.toString()

    val vm:VM = hiltViewModel()

    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val settingsResultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            Log.d("appDebug", "Location settings enabled")
            locationSettingsChecked = true
        } else {
            Log.d("appDebug", "Location settings not enabled")
            locationSettingsChecked = false
        }
    }
    val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    val client = LocationServices.getSettingsClient(context)
    val task = client.checkLocationSettings(builder.build())
    if(!locationSettingsRequested){
        task.addOnFailureListener { exception ->
            //locationSettingsChecked = false
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    settingsResultLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e("appDebug", "Failed to send intent", sendEx)
                }
            }
            locationSettingsRequested=true
        }
    }
    task.addOnSuccessListener {
        locationSettingsChecked = true
    }

    if(locationSettingsChecked){
        RequestLocationPermission(

            onPermissionGranted = {
                showPermissionResultText = true
                getCurrentLocation(
                    context =context,
                    fusedLocationProviderClient = fusedLocationProviderClient,
                    onGetCurrentLocationSuccess = {

                        latitude = it.first
                        longitude = it.second
                        locationText =
                            "Location using LAST-LOCATION: LATITUDE: ${latitude}, LONGITUDE: ${longitude}"

                    },
                    onGetCurrentLocationFailed = {  exception ->
                        showPermissionResultText = true
                        locationText = exception.localizedMessage ?: "Error Getting Last Location"
                    }
                )
            },
            onPermissionDenied = {
                showPermissionResultText = true
                permissionResultText = "Permission Denied :("
            },
            onPermissionsRevoked = {
                showPermissionResultText = true
                permissionResultText = "Permission Revoked :("
            }
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            // Display permission result and location information if available
            if (showPermissionResultText) {
                if(latitude !=0.0 && longitude !=0.0){
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                        CircularProgressIndicator()
                    }
                    Log.d("loccc","$latitude")
                    val(address,exactaddress) = getReadLoc(latitude,longitude,context)
                    Log.d("loccc", address)
                    if(!address.isNullOrEmpty()){
                        vm.AddOrUpdateAddress("",email,address,exactaddress,context){
                            navController.navigate("MainPage")
                        }
                    }
                }

                Text(text = permissionResultText, textAlign = TextAlign.Center)
                Text(text = locationText, textAlign = TextAlign.Center)
            }else{
                Text(text = "Please grant location permission", textAlign = TextAlign.Center)
            }
        }
    }else if(!locationSettingsChecked){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = "Please Grant location Permission")
        }
    }


}

private fun areLocationPermissionsGranted(context: Context): Boolean {

    return (ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            )
}


@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    context: Context,
    fusedLocationProviderClient: FusedLocationProviderClient,
    onGetCurrentLocationSuccess: (Pair<Double, Double>) -> Unit,
    onGetCurrentLocationFailed: (Exception) -> Unit,
    priority: Boolean = true
){
    val accuracy = if (priority){
        LocationRequest.PRIORITY_HIGH_ACCURACY
    } else LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

    if (areLocationPermissionsGranted(context)) {
        fusedLocationProviderClient.getCurrentLocation(
            accuracy, CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            location?.let {
                onGetCurrentLocationSuccess(Pair(it.latitude, it.longitude))
            }
        }.addOnFailureListener { exception ->
            // If an error occurs, invoke the failure callback with the exception
            onGetCurrentLocationFailed(exception)
        }

    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onPermissionsRevoked: () -> Unit
) {
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )
    LaunchedEffect(key1 = permissionState){
        val allPermissionsRevoked = permissionState.permissions.size ==permissionState.revokedPermissions.size

        val permissionsToRequest = permissionState.permissions.filter {
            !it.status.isGranted
        }

        if(permissionsToRequest.isNotEmpty()){
            permissionState.launchMultiplePermissionRequest()
        }

        if(allPermissionsRevoked){
            onPermissionsRevoked()
        }else{
            if (permissionState.allPermissionsGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }
    }
}

fun getReadLoc(latitute:Double, longitude:Double,context: Context):Pair<String, String> {
    val geocoder = Geocoder(context, Locale.getDefault())
    var addressString = ""
    var addresssearch = ""
    try {
        val addressList: MutableList<Address>? = geocoder.getFromLocation(latitute, longitude, 1)

        // use your lat, long value here
        if (addressList != null && addressList.isNotEmpty()) {
            val address = addressList[0]
            val sb = StringBuilder()
            for (i in 0 until address.maxAddressLineIndex) {
                sb.append(address.getAddressLine(i)).append("\n")
            }

            // Various Parameters of an Address are appended
            // to generate a complete Address
            if (address.premises != null)
                sb.append(address.premises).append(", ")

            sb.append(address.subAdminArea).append("\n")
            sb.append(address.locality).append(", ")
            sb.append(address.adminArea).append(", ")
            sb.append(address.countryName).append(", ")
            sb.append(address.postalCode)


            addresssearch = " ${address.locality}, ${address.adminArea}, ${address.countryName}"

            // StringBuilder sb is converted into a string
            // and this value is assigned to the
            // initially declared addressString string.
            addressString = sb.toString()

            Log.d("geolocation", addressString)
        }
    } catch (e: IOException) {
        // Toast.makeText(applicationContext,"Unable connect to Geocoder",Toast.LENGTH_LONG).show()
    }
    return  Pair(addresssearch, addressString)

}


