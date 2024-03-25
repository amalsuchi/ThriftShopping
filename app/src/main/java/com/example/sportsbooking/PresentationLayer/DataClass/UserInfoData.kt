package com.example.sportsbooking.PresentationLayer.DataClass

data class UserInfoData(
    var name :String? = null,
    val email :String? = null,
    val uid:String? = null,
    val latitude:Double? = null,
    val longitude: Double? = null,
    val approxGeolocation:String? = null,
    val exactGeolocation:String? = null,
    val wishlist:List<String>? =null
)
