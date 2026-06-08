package com.example.memojimatch.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    fun format(timestamp: Long): String {
        val sdf = SimpleDateFormat("d. M. yyyy.", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
