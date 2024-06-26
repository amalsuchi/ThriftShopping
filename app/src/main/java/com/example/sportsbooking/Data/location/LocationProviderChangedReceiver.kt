package com.example.sportsbooking.Data.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log

class LocationProviderChangedReceiver  : BroadcastReceiver() {
    private var isGpsEnabled: Boolean = false
    private var isNetworkEnabled: Boolean = false
    private var locationListener: LocationListener? = null

    fun init(locationListener: LocationListener){
        this.locationListener = locationListener
    }

    override fun onReceive(context: Context,intent: Intent){
        intent.action?.let { act ->
            if(act.matches("android.location.PROVIDERS_CHANGED".toRegex())){
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                Log.i(TAG, "Location Providers changed, is GPS Enabled: $isGpsEnabled")


                if (isGpsEnabled && isNetworkEnabled) {
                    locationListener?.onEnabled()
                } else {
                    locationListener?.onDisabled()
                }
            }
        }
    }
    interface LocationListener {
        fun onEnabled()
        fun onDisabled()
    }

    companion object {
        private val TAG = "Location"
    }
}