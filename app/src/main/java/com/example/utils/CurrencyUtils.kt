package com.example.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyUtils {
    private val standardFormat = DecimalFormat("#,##0", DecimalFormatSymbols(Locale.US))
    private val decimalFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))

    fun formatAmount(amount: Double, currency: String = "৳", useBengaliDigits: Boolean = true): String {
        val isWhole = (amount % 1.0) == 0.0
        val formattedNumber = if (isWhole) {
            standardFormat.format(amount)
        } else {
            decimalFormat.format(amount)
        }

        val resultNumber = if (useBengaliDigits) {
            DateUtils.toBengaliDigits(formattedNumber)
        } else {
            formattedNumber
        }

        return "$currency $resultNumber"
    }

    fun formatPlainNumber(amount: Double, useBengaliDigits: Boolean = true): String {
        val isWhole = (amount % 1.0) == 0.0
        val formattedNumber = if (isWhole) {
            standardFormat.format(amount)
        } else {
            decimalFormat.format(amount)
        }
        return if (useBengaliDigits) DateUtils.toBengaliDigits(formattedNumber) else formattedNumber
    }
}
