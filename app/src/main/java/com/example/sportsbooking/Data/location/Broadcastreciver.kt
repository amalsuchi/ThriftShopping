package com.example.sportsbooking.Data.location

import android.content.Context
import android.content.IntentFilter
import android.location.LocationManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat.registerReceiver
import com.example.sportsbooking.PresentationLayer.ViewModel.VM

 fun registerBroadcastReceiver(context: Context, vm:VM,) {
     var br = LocationProviderChangedReceiver()
     br!!.init(
         object : LocationProviderChangedReceiver.LocationListener {
             override fun onEnabled() {
                 vm.isLocationEnabled.value = true//Update our VM
             }

             override fun onDisabled() {
                 vm.isLocationEnabled.value = false//Update our VM
             }
         }
     )
     val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
    context.registerReceiver(br, filter)
}