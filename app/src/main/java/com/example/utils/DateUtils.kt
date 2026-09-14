package com.example.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val isoTimeFormat = SimpleDateFormat("HH:mm", Locale.US)

    fun getCurrentDate(): String = isoDateFormat.format(Date())
    fun getCurrentTime(): String = isoTimeFormat.format(Date())

    fun toBengaliDigits(input: String): String {
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(bnDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toBengaliNumber(number: Number): String {
        return toBengaliDigits(number.toString())
    }

    val bengaliMonths = listOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )

    fun formatBengaliDate(isoDate: String): String {
        return try {
            val parts = isoDate.split("-")
            if (parts.size == 3) {
                val year = toBengaliDigits(parts[0])
                val monthIndex = parts[1].toInt() - 1
                val day = toBengaliDigits(parts[2].toInt().toString())
                val monthName = bengaliMonths.getOrElse(monthIndex) { parts[1] }
                "$day $monthName $year"
            } else {
                toBengaliDigits(isoDate)
            }
        } catch (e: Exception) {
            isoDate
        }
    }

    fun formatBengaliTime(isoTime: String): String {
        return try {
            val parts = isoTime.split(":")
            if (parts.size >= 2) {
                var hour = parts[0].toInt()
                val min = parts[1]
                val period = if (hour < 12) "সকাল" else if (hour < 16) "দুপুর" else if (hour < 19) "বিকাল" else "রাত"
                if (hour == 0) hour = 12
                else if (hour > 12) hour -= 12
                "$period ${toBengaliDigits(hour.toString())}:${toBengaliDigits(min)}"
            } else {
                toBengaliDigits(isoTime)
            }
        } catch (e: Exception) {
            isoTime
        }
    }

    fun getStartOfToday(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    fun getEndOfToday(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 23)
        c.set(Calendar.MINUTE, 59)
        c.set(Calendar.SECOND, 59)
        c.set(Calendar.MILLISECOND, 999)
        return c.timeInMillis
    }

    fun getStartOfYesterday(): Long {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, -1)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    fun getEndOfYesterday(): Long {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, -1)
        c.set(Calendar.HOUR_OF_DAY, 23)
        c.set(Calendar.MINUTE, 59)
        c.set(Calendar.SECOND, 59)
        c.set(Calendar.MILLISECOND, 999)
        return c.timeInMillis
    }

    fun getStartOfWeek(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_WEEK, c.firstDayOfWeek)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    fun getStartOfMonth(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_MONTH, 1)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    fun getEndOfMonth(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
        c.set(Calendar.HOUR_OF_DAY, 23)
        c.set(Calendar.MINUTE, 59)
        c.set(Calendar.SECOND, 59)
        c.set(Calendar.MILLISECOND, 999)
        return c.timeInMillis
    }

    fun getStartOfLastMonth(): Long {
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, -1)
        c.set(Calendar.DAY_OF_MONTH, 1)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    fun getEndOfLastMonth(): Long {
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, -1)
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
        c.set(Calendar.HOUR_OF_DAY, 23)
        c.set(Calendar.MINUTE, 59)
        c.set(Calendar.SECOND, 59)
        c.set(Calendar.MILLISECOND, 999)
        return c.timeInMillis
    }

    fun getDaysInCurrentMonth(): Int {
        val c = Calendar.getInstance()
        return c.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getCurrentDayOfMonth(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }

    fun getCurrentMonthNameBn(): String {
        val monthIdx = Calendar.getInstance().get(Calendar.MONTH)
        return bengaliMonths.getOrElse(monthIdx) { "" }
    }
}
