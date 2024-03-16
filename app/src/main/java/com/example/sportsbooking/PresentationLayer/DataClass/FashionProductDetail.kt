package com.example.sportsbooking.PresentationLayer.DataClass

import java.sql.Timestamp

data class FashionProductDetailData(
    var Detail:String? = null,
    var For:String? = null,
    var Location:String? = null,
    var  Price:Int? = null,
    var Size :String? = null,
    var imageUri:List<String>? = null,
    var name:String? = null
)
