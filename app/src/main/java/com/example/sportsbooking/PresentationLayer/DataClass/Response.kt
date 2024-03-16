package com.example.sportsbooking.PresentationLayer.DataClass

import android.service.autofill.UserData
import com.google.firebase.firestore.DocumentSnapshot

data class Response(
    val isLoading:Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val user: com.example.sportsbooking.PresentationLayer.DataClass.UserData? = null,
    val location : String? = null,
    var userAdds: MutableList<DocumentSnapshot>? =null,
    var FashionUserProductData : FashionProductDetailData? = null
)
