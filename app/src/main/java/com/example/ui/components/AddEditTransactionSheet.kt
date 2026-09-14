package com.example.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CategoryEntity
import com.example.data.TransactionEntity
import com.example.model.CategoryHelper
import com.example.model.TransactionType
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.utils.DateUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionSheet(
    initialTransaction: TransactionEntity?,
    initialType: TransactionType,
    categories: List<CategoryEntity>,
    currencySymbol: String = "৳",
    onSave: (
        id: Long,
        type: String,
        category: String,
        amount: Double,
        date: String,
        time: String,
        timestamp: Long,
        note: String
    ) -> Unit,
    onAddCustomCategory: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var selectedType by remember {
        mutableStateOf(
            if (initialTransaction != null) {
                if (initialTransaction.type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE
            } else {
                initialType
            }
        )
    }

    var amountText by remember {
        mutableStateOf(
            if (initialTransaction != null) {
                if (initialTransaction.amount % 1.0 == 0.0) {
                    initialTransaction.amount.toInt().toString()
                } else {
                    initialTransaction.amount.toString()
                }
            } else ""
        )
    }

    val filteredCategories = categories.filter { it.type == selectedType.name }

    var selectedCategory by remember {
        mutableStateOf(
            initialTransaction?.category ?: filteredCategories.firstOrNull()?.name ?: "অন্যান্য"
        )
    }

    var selectedDate by remember {
        mutableStateOf(initialTransaction?.date ?: DateUtils.getCurrentDate())
    }

    var selectedTime by remember {
        mutableStateOf(initialTransaction?.time ?: DateUtils.getCurrentTime())
    }

    var noteText by remember {
        mutableStateOf(initialTransaction?.note ?: "")
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (initialTransaction == null) "নতুন হিসাব যোগ করুন" else "হিসাব সম্পাদনা করুন",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "বন্ধ করুন",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Income / Expense Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Expense Tab
                val isExpense = selectedType == TransactionType.EXPENSE
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            selectedType = TransactionType.EXPENSE
                            val defaultExpense = categories.firstOrNull { it.type == "EXPENSE" }?.name ?: "খাবার"
                            selectedCategory = defaultExpense
                        }
                        .testTag("type_expense_tab"),
                    color = if (isExpense) ExpenseRed else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "নতুন খরচ",
                        color = if (isExpense) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp
                        ),
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .align(Alignment.CenterVertically),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // Income Tab
                val isIncome = selectedType == TransactionType.INCOME
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            selectedType = TransactionType.INCOME
                            val defaultIncome = categories.firstOrNull { it.type == "INCOME" }?.name ?: "বেতন"
                            selectedCategory = defaultIncome
                        }
                        .testTag("type_income_tab"),
                    color = if (isIncome) IncomeGreen else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "নতুন আয়",
                        color = if (isIncome) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp
                        ),
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .align(Alignment.CenterVertically),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Amount Input
            Text(
                text = "টাকার পরিমাণ",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    // Convert Bengali numerals to English digits if typed
                    val converted = input
                        .replace('০', '0')
                        .replace('১', '1')
                        .replace('২', '2')
                        .replace('৩', '3')
                        .replace('৪', '4')
                        .replace('৫', '5')
                        .replace('৬', '6')
                        .replace('৭', '7')
                        .replace('৮', '8')
                        .replace('৯', '9')
                    if (converted.isEmpty() || converted.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        amountText = converted
                        errorMessage = null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("amount_input"),
                placeholder = { Text("০.০০", fontSize = 18.sp) },
                leadingIcon = {
                    Text(
                        text = currencySymbol,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selectedType == TransactionType.EXPENSE) ExpenseRed else IncomeGreen
                        ),
                        modifier = Modifier.padding(start = 14.dp, end = 4.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (selectedType == TransactionType.EXPENSE) ExpenseRed else IncomeGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Preset Amount Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(100, 500, 1000, 5000).forEach { preset ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clickable {
                                val current = amountText.toDoubleOrNull() ?: 0.0
                                val newVal = current + preset
                                amountText = if (newVal % 1.0 == 0.0) newVal.toInt().toString() else newVal.toString()
                                errorMessage = null
                            }
                    ) {
                        Text(
                            text = "+$currencySymbol ${DateUtils.toBengaliDigits(preset.toString())}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Category Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ক্যাটাগরি নির্বাচন করুন",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "+ নতুন ক্যাটাগরি",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .clickable { onAddCustomCategory() }
                        .padding(4.dp)
                        .testTag("add_custom_category_button")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips Grid
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredCategories.forEach { category ->
                    val isSelected = selectedCategory == category.name
                    val categoryColor = CategoryHelper.getCategoryColor(category.name, selectedType == TransactionType.EXPENSE)
                    val icon = CategoryHelper.getCategoryIcon(category.iconName, category.name)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) categoryColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) categoryColor else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategory = category.name }
                            .testTag("category_chip_${category.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = category.name,
                                tint = if (isSelected) categoryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                ),
                                color = if (isSelected) categoryColor else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Date & Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date Picker Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .clickable {
                            val parts = selectedDate.split("-")
                            val cal = Calendar.getInstance()
                            val yr = parts.getOrNull(0)?.toIntOrNull() ?: cal.get(Calendar.YEAR)
                            val mo = (parts.getOrNull(1)?.toIntOrNull() ?: (cal.get(Calendar.MONTH) + 1)) - 1
                            val da = parts.getOrNull(2)?.toIntOrNull() ?: cal.get(Calendar.DAY_OF_MONTH)

                            DatePickerDialog(context, { _, y, m, d ->
                                selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                            }, yr, mo, da).show()
                        }
                        .testTag("date_picker_button"),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "তারিখ",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "তারিখ", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = DateUtils.formatBengaliDate(selectedDate), style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold), maxLines = 1)
                        }
                    }
                }

                // Time Picker Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .clickable {
                            val parts = selectedTime.split(":")
                            val cal = Calendar.getInstance()
                            val hr = parts.getOrNull(0)?.toIntOrNull() ?: cal.get(Calendar.HOUR_OF_DAY)
                            val mn = parts.getOrNull(1)?.toIntOrNull() ?: cal.get(Calendar.MINUTE)

                            TimePickerDialog(context, { _, h, m ->
                                selectedTime = String.format("%02d:%02d", h, m)
                            }, hr, mn, false).show()
                        }
                        .testTag("time_picker_button"),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "সময়",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "সময়", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = DateUtils.formatBengaliTime(selectedTime), style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold), maxLines = 1)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Note / Description Field
            Text(
                text = "বিবরণ / নোট (ঐচ্ছিক)",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("note_input"),
                placeholder = { Text("যেমন: রেস্তোরাঁয় দুপুরের খাবার, মাসিক বেতন...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Error Message
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = ExpenseRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        errorMessage = "অনুগ্রহ করে সঠিক টাকার পরিমাণ লিখুন"
                        return@Button
                    }
                    if (selectedCategory.isBlank()) {
                        errorMessage = "অনুগ্রহ করে একটি ক্যাটাগরি নির্বাচন করুন"
                        return@Button
                    }

                    // Compute timestamp from date and time
                    val cal = Calendar.getInstance()
                    try {
                        val dParts = selectedDate.split("-")
                        val tParts = selectedTime.split(":")
                        cal.set(
                            dParts[0].toInt(),
                            dParts[1].toInt() - 1,
                            dParts[2].toInt(),
                            tParts.getOrNull(0)?.toInt() ?: 0,
                            tParts.getOrNull(1)?.toInt() ?: 0,
                            0
                        )
                    } catch (e: Exception) {
                        // fallback to current
                    }

                    onSave(
                        initialTransaction?.id ?: 0L,
                        selectedType.name,
                        selectedCategory,
                        amount,
                        selectedDate,
                        selectedTime,
                        cal.timeInMillis,
                        noteText
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_transaction_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == TransactionType.EXPENSE) ExpenseRed else IncomeGreen
                )
            ) {
                Text(
                    text = if (initialTransaction == null) "হিসাব সংরক্ষণ করুন" else "হিসাব আপডেট করুন",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}
