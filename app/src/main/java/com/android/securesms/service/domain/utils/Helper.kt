package com.android.securesms.service.domain.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun currentTime(): String {
    val currentTimeMillis = System.currentTimeMillis()
    val currentDate = Date(currentTimeMillis)
    val dateFormat = SimpleDateFormat("dd.MMM.yyyy-hh.mm.ss.a", Locale.getDefault())
    return dateFormat.format(currentDate)
}

fun formatDateToString(date: Date): String {
    // Create a SimpleDateFormat instance with the desired format
    val dateFormat = SimpleDateFormat("dd.MMM.yyyy-hh.mm.ss.a", Locale.getDefault())

    // Format the date to the desired string
    return dateFormat.format(date)
}

fun convertTime(unixTime: Long): String {
    Log.d("convertTime", "convertTime: $unixTime")
    val date = Date(unixTime)
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault())
    return dateFormat.format(date)
}