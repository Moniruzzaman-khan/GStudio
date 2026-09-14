package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.TransactionEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ExportImportUtils {

    fun exportToCsv(transactions: List<TransactionEntity>): String {
        val sb = StringBuilder()
        sb.append("ID,Type,Category,Amount,Date,Time,Timestamp,Note\n")
        for (tx in transactions) {
            val safeNote = tx.note.replace("\"", "\"\"")
            val safeCategory = tx.category.replace("\"", "\"\"")
            sb.append("${tx.id},\"${tx.type}\",\"$safeCategory\",${tx.amount},\"${tx.date}\",\"${tx.time}\",${tx.timestamp},\"$safeNote\"\n")
        }
        return sb.toString()
    }

    fun exportToJson(transactions: List<TransactionEntity>): String {
        val jsonArray = JSONArray()
        for (tx in transactions) {
            val obj = JSONObject()
            obj.put("id", tx.id)
            obj.put("type", tx.type)
            obj.put("category", tx.category)
            obj.put("amount", tx.amount)
            obj.put("date", tx.date)
            obj.put("time", tx.time)
            obj.put("timestamp", tx.timestamp)
            obj.put("note", tx.note)
            jsonArray.put(obj)
        }
        val root = JSONObject()
        root.put("app", "আমার দৈনিক খরচ")
        root.put("version", "1.0")
        root.put("exportedAt", System.currentTimeMillis())
        root.put("transactions", jsonArray)
        return root.toString(2)
    }

    fun parseJson(jsonString: String): List<TransactionEntity> {
        val list = mutableListOf<TransactionEntity>()
        try {
            val root = JSONObject(jsonString)
            val array = if (root.has("transactions")) {
                root.getJSONArray("transactions")
            } else {
                JSONArray(jsonString)
            }
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TransactionEntity(
                        id = 0, // Generate new local ID on import
                        type = obj.optString("type", "EXPENSE"),
                        category = obj.optString("category", "অন্যান্য"),
                        amount = obj.optDouble("amount", 0.0),
                        date = obj.optString("date", DateUtils.getCurrentDate()),
                        time = obj.optString("time", DateUtils.getCurrentTime()),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        note = obj.optString("note", "")
                    )
                )
            }
        } catch (e: Exception) {
            // Try fallback array parsing
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TransactionEntity(
                        id = 0,
                        type = obj.optString("type", "EXPENSE"),
                        category = obj.optString("category", "অন্যান্য"),
                        amount = obj.optDouble("amount", 0.0),
                        date = obj.optString("date", DateUtils.getCurrentDate()),
                        time = obj.optString("time", DateUtils.getCurrentTime()),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        note = obj.optString("note", "")
                    )
                )
            }
        }
        return list
    }

    fun parseCsv(csvString: String): List<TransactionEntity> {
        val list = mutableListOf<TransactionEntity>()
        val lines = csvString.lines()
        if (lines.isEmpty()) return list

        for (i in 1 until lines.size) {
            val line = lines[i].trim()
            if (line.isEmpty()) continue
            val tokens = parseCsvLine(line)
            if (tokens.size >= 6) {
                val type = tokens.getOrNull(1) ?: "EXPENSE"
                val category = tokens.getOrNull(2) ?: "অন্যান্য"
                val amount = tokens.getOrNull(3)?.toDoubleOrNull() ?: 0.0
                val date = tokens.getOrNull(4) ?: DateUtils.getCurrentDate()
                val time = tokens.getOrNull(5) ?: DateUtils.getCurrentTime()
                val timestamp = tokens.getOrNull(6)?.toLongOrNull() ?: System.currentTimeMillis()
                val note = tokens.getOrNull(7) ?: ""
                list.add(
                    TransactionEntity(
                        id = 0,
                        type = type,
                        category = category,
                        amount = amount,
                        date = date,
                        time = time,
                        timestamp = timestamp,
                        note = note
                    )
                )
            }
        }
        return list
    }

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                    sb.append('\"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString())
                sb.clear()
            } else {
                sb.append(c)
            }
            i++
        }
        tokens.add(sb.toString())
        return tokens
    }

    fun shareExportedFile(context: Context, fileName: String, content: String, mimeType: String) {
        try {
            val cacheFile = File(context.cacheDir, fileName)
            FileOutputStream(cacheFile).use { it.write(content.toByteArray(Charsets.UTF_8)) }

            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cacheFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, "আমার দৈনিক খরচ - ডাটা ব্যাকআপ")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "ফাইল এক্সপোর্ট করুন"))
        } catch (e: Exception) {
            // Fallback plain text share if FileProvider is not set up
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, content)
                putExtra(Intent.EXTRA_SUBJECT, fileName)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "ফাইল শেয়ার করুন"))
        }
    }

    fun readStream(inputStream: InputStream): String {
        return inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }
}
