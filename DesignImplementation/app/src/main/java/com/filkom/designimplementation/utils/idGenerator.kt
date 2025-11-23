package com.filkom.designimplementation.utils

import kotlin.random.Random

object IdGenerator {
    fun generateUniqueId(): Long {
        val timestamp = System.currentTimeMillis()
        val randomPart = Random.nextInt(100, 999)
        val uniqueIdString = "$timestamp$randomPart"
        return uniqueIdString.toLong()
    }
}