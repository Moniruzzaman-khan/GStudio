package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.TransactionEntity
import com.example.utils.CurrencyUtils
import com.example.utils.DateUtils
import com.example.utils.ExportImportUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("আমার দৈনিক খরচ", appName)
    }

    @Test
    fun `currency utils formats amounts with bengali digits`() {
        val formatted = CurrencyUtils.formatAmount(850.0, "৳")
        assertEquals("৳ ৮৫০", formatted)

        val formattedWithDecimal = CurrencyUtils.formatAmount(1250.50, "৳")
        assertEquals("৳ ১,২৫০.৫০", formattedWithDecimal)
    }

    @Test
    fun `date utils converts english digits to bengali digits`() {
        val bengali = DateUtils.toBengaliDigits("0123456789")
        assertEquals("০১২৩৪৫৬৭৮৯", bengali)
    }

    @Test
    fun `export and import transactions CSV works correctly`() {
        val transactions = listOf(
            TransactionEntity(
                id = 1L,
                type = "EXPENSE",
                category = "খাবার",
                amount = 250.0,
                date = "2026-09-14",
                time = "13:30",
                timestamp = 1726300000000L,
                note = "দুপুরের খাবার"
            )
        )
        val csv = ExportImportUtils.exportToCsv(transactions)
        assertTrue(csv.contains("EXPENSE"))
        assertTrue(csv.contains("খাবার"))

        val parsed = ExportImportUtils.parseCsv(csv)
        assertEquals(1, parsed.size)
        assertEquals("EXPENSE", parsed[0].type)
        assertEquals("খাবার", parsed[0].category)
        assertEquals(250.0, parsed[0].amount, 0.01)
        assertEquals("দুপুরের খাবার", parsed[0].note)
    }
}
