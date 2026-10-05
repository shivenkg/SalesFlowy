package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object IndiaLocaleUtil {
    val INDIA_LOCALE = Locale("en", "IN")
    private val IST_TIMEZONE = TimeZone.getTimeZone("Asia/Kolkata")

    fun formatInr(amount: Double): String {
        return when {
            amount >= 10_000_000 -> {
                val cr = amount / 10_000_000.0
                "₹${String.format(INDIA_LOCALE, "%.2f", cr)} Cr"
            }
            amount >= 100_000 -> {
                val lk = amount / 100_000.0
                "₹${String.format(INDIA_LOCALE, "%.2f", lk)} L"
            }
            else -> {
                val formatter = NumberFormat.getNumberInstance(INDIA_LOCALE)
                "₹${formatter.format(amount.toLong())}"
            }
        }
    }

    fun formatInrExact(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(INDIA_LOCALE)
        return "₹${formatter.format(amount.toLong())}"
    }

    fun formatInrShort(amount: Double): String {
        return when {
            amount >= 10_000_000 -> "₹${String.format(INDIA_LOCALE, "%.1f", amount / 10_000_000.0)} Cr"
            amount >= 100_000 -> "₹${String.format(INDIA_LOCALE, "%.1f", amount / 100_000.0)} L"
            amount >= 1000 -> "₹${(amount / 1000).toInt()}k"
            else -> "₹${amount.toInt()}"
        }
    }

    fun formatPhone(phone: String): String {
        val clean = phone.replace("+91", "").trim().filter { it.isDigit() }.takeLast(10)
        return if (clean.length == 10) {
            "+91 ${clean.substring(0, 5)} ${clean.substring(5)}"
        } else if (phone.startsWith("+91")) {
            phone
        } else {
            "+91 $phone"
        }
    }

    fun formatIstDate(date: Date = Date()): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a 'IST'", INDIA_LOCALE)
        sdf.timeZone = IST_TIMEZONE
        return sdf.format(date)
    }

    fun formatIstTime(date: Date = Date()): String {
        val sdf = SimpleDateFormat("hh:mm a", INDIA_LOCALE)
        sdf.timeZone = IST_TIMEZONE
        return sdf.format(date)
    }
}
