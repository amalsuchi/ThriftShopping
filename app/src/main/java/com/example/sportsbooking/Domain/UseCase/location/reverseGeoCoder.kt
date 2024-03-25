package com.example.sportsbooking.Domain.UseCase.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sportsbooking.PresentationLayer.ViewModel.VM
import java.io.IOException
import java.util.Locale

fun reverseGeoCoder(latitute:Double, longitude:Double,context: Context):Pair<String, String> {
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