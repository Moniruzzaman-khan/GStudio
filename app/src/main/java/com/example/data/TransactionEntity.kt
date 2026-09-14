package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "EXPENSE" or "INCOME"
    val category: String,
    val amount: Double,
    val date: String, // Format "YYYY-MM-DD"
    val time: String, // Format "HH:mm"
    val timestamp: Long, // Epoch milliseconds for sorting
    val note: String = ""
)
