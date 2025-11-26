package com.filkom.designimplementation.utils

import com.filkom.designimplementation.model.common.AppBookingDate
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale


data class TimeSlot(
    val time: String,
    val isAvailable: Boolean
)
object DateHelper {

    fun getNext7Days(): List<AppBookingDate> {
        val list = mutableListOf<AppBookingDate>()
        val calendar = Calendar.getInstance()
        val locale = Locale("id", "ID")

        // Format disiapkan di luar loop agar lebih efisien
        val dayFormat = SimpleDateFormat("EEE", locale)
        val dateFormat = SimpleDateFormat("dd", locale)
        val fullFormat = SimpleDateFormat("EEEE, d MMMM yyyy", locale)

        for (i in 0..6) {
            val dateObj = calendar.time

            list.add(
                AppBookingDate(
                    day = dayFormat.format(dateObj),
                    date = dateFormat.format(dateObj),
                    fullDate = fullFormat.format(dateObj)
                )
            )

            // Geser ke hari berikutnya
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return list
    }

    fun generateTimeSlots(isToday: Boolean): List<TimeSlot> {
        val slots = mutableListOf<TimeSlot>()
        val startHour = 9  // Buka jam 09.00
        val endHour = 20   // Tutup jam 20.00

        // Ambil jam sekarang (Hanya jam-nya saja, misal 14)
        // Jika HP user versi lama (bawah Android O), ganti LocalTime dengan Calendar
        val currentHour = LocalTime.now().hour

        for (hour in startHour..endHour) {
            val timeString = String.format("%02d.00", hour) // Format "09.00"

            // Logic: Jika HARI INI, cek apakah jam slot < jam sekarang?
            // Jika YA, berarti sudah lewat (false). Jika TIDAK, berarti available (true).
            // Jika BUKAN HARI INI (Besok dst), semua jam available.
            val isAvailable = if (isToday) {
                hour > currentHour
            } else {
                true
            }

            slots.add(TimeSlot(timeString, isAvailable))
        }
        return slots
    }
}