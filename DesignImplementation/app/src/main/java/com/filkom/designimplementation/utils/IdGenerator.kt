package com.filkom.designimplementation.utils

import kotlin.random.Random

object IdGenerator {
    fun generateUniqueId(): Long {
        val timestamp = System.currentTimeMillis()
        val randomPart = Random.nextInt(100, 999)
        val uniqueIdString = "$timestamp$randomPart"
        return uniqueIdString.toLong()
    }
    fun generateUniqueIdHistory(): String {
        val timestamp = System.currentTimeMillis()
        val randomPart = Random.nextInt(1000, 9999)
        val uniqueIdString = "$timestamp$randomPart"
        return "HSTR-" + uniqueIdString.toLong()
    }
}