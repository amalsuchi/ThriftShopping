package com.example.sportsbooking.PresentationLayer.DataClass

data class UiResponse(
    val isLoading:Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val userInfoData: UserInfoData? = null
)