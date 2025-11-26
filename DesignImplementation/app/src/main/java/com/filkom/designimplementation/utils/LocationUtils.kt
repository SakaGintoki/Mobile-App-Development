package com.filkom.designimplementation.utils

import android.location.Location

object LocationUtils {
    // Menghitung jarak dalam Kilometer
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val startPoint = Location("locationA")
        startPoint.latitude = lat1
        startPoint.longitude = lon1

        val endPoint = Location("locationB")
        endPoint.latitude = lat2
        endPoint.longitude = lon2

        val distanceInMeters = startPoint.distanceTo(endPoint)
        return distanceInMeters / 1000 // Konversi ke KM
    }
}