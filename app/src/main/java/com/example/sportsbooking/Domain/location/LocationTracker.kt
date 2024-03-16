package com.example.sportsbooking.Domain.location

import android.location.Location
import com.google.protobuf.DescriptorProtos

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
}