package com.example.sportsbooking.Domain.UseCase.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.IntentFilter
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sportsbooking.Data.location.LocationProviderChangedReceiver
import com.example.sportsbooking.Data.location.registerBroadcastReceiver
import com.example.sportsbooking.PresentationLayer.ViewModel.VM
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource


@Composable

fun getLatLong(){
    val context = LocalContext.current
    val vm: VM = hiltViewModel()
    var br: LocationProviderChangedReceiver? = null
    registerBroadcastReceiver(context,vm)
    var locationText by remember { mutableStateOf("") }
    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract =  ActivityResultContracts.StartIntentSenderForResult(),
        ){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            Log.d("Location", "Location settings enabled or adjusted")
        }
        else{
            locationText = "Location settings not enabled"
        }

    }

    val isLocationEnabled by vm.isLocationEnabled.collectAsState()
    if(!isLocationEnabled){
        vm.enableLocationRequest(context) { intentSenderRequest ->
            locationSettingsLauncher.launch(intentSenderRequest)
        }
    }else{

        requestLocationPermission(
            onPermissionGranted = { getCurrentData(
                fusedLocationProviderClient,
                context,
                onGetCurrentLocationSuccess = {
                    val(addressSearch, address) = reverseGeoCoder(it.first,it.second,context)
                    if(!address.isNullOrEmpty()){
                        vm.updateLocation(it.first,it.second,addressSearch,address)
                        Log.d("list","$address")
                    }

                },
                onGetCurrentLocationFailed = {exception ->
                    locationText = exception.localizedMessage ?: "Error getting location"
                }
            ) },
            onPermissionDenied = {
                locationText = "Please enable location to use app"
            },
            onPermissionsRevoked = {
                locationText = "Please enable location permission"
            }
        )
    }

/*


    var locationSettingsChecked by remember { mutableStateOf(false) }





    val settingsResultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            Log.d("appDebug", "Location settings enabled")

        } else {
            Log.d("appDebug", "Location settings not enabled")

        }
    }

    //request location
    val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    val client = LocationServices.getSettingsClient(context)
    val task = client.checkLocationSettings(builder.build())
    task.addOnSuccessListener {
        locationSettingsChecked = true
    }
    task.addOnFailureListener {exception ->
        if(exception is ResolvableApiException){
            try {
                val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                settingsResultLauncher.launch(intentSenderRequest)
            } catch (sendEx: IntentSender.SendIntentException) {
                Log.e("appDebug", "Failed to send intent", sendEx)
            }
        }
    }

    Log.d("listdata","$locationSettingsChecked")
    if(locationSettingsChecked) {

    }

 */


}

@SuppressLint("MissingPermission")
fun getCurrentData(fusedLocationProviderClient: FusedLocationProviderClient,
                   context: Context,
                   onGetCurrentLocationSuccess: (Pair<Double, Double>) -> Unit,
                   onGetCurrentLocationFailed: (Exception) -> Unit,
                   priority: Boolean = true
){
    val accuracy = if (priority) Priority.PRIORITY_HIGH_ACCURACY
    else Priority.PRIORITY_BALANCED_POWER_ACCURACY

    if(areLocationPermissionsGranted(context)){
        fusedLocationProviderClient.getCurrentLocation(
            accuracy, CancellationTokenSource().token,
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

private fun areLocationPermissionsGranted(context: Context): Boolean {
    return (ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
}