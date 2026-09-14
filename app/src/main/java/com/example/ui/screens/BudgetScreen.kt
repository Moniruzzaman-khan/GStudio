package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DashboardSummary
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.WarningAmber
import com.example.utils.CurrencyUtils
import com.example.utils.DateUtils

@Composable
fun BudgetScreen(
    summary: DashboardSummary,
    currencySymbol: String = "৳",
    onUpdateBudget: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditBudgetDialogOpen by remember { mutableStateOf(false) }
    var budgetInputText by remember {
        mutableStateOf(
            if (summary.monthlyBudget % 1.0 == 0.0) summary.monthlyBudget.toInt().toString() else summary.monthlyBudget.toString()
        )
    }

    val progress = (summary.budgetUsedPercent / 100f).coerceIn(0f, 1f)
    val statusColor = when {
        summary.isOverBudget -> ExpenseRed
        summary.isNearBudgetWarning -> WarningAmber
        else -> Color(0xFF00796B)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("budget_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "মাসিক বাজেট ব্যবস্থাপনা",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "আপনার নির্ধারিত খরচের লক্ষ্য ও বর্তমান অবস্থা",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Budget Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("budget_overview_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "মাসিক বাজেট",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyUtils.formatAmount(summary.monthlyBudget, currencySymbol),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 26.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = {
                                budgetInputText = if (summary.monthlyBudget % 1.0 == 0.0) summary.monthlyBudget.toInt().toString() else summary.monthlyBudget.toString()
                                isEditBudgetDialogOpen = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier.testTag("edit_budget_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "বাজেট পরিবর্তন",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ব্যবহৃত: ${DateUtils.toBengaliDigits(String.format("%.1f", summary.budgetUsedPercent))}%",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = statusColor
                        )
                        Text(
                            text = if (summary.isOverBudget) "বাজেট অতিক্রান্ত" else "বাকি আছে",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = statusColor,
                        trackColor = statusColor.copy(alpha = 0.15f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3 Metric Boxes (Budget, Spent, Remaining)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Budget
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "বাজেট", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = CurrencyUtils.formatAmount(summary.monthlyBudget, currencySymbol),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                        }

                        // Spent
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = ExpenseRed.copy(alpha = 0.1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "খরচ হয়েছে", fontSize = 12.sp, color = ExpenseRed)
                                Text(
                                    text = CurrencyUtils.formatAmount(summary.monthlyExpense, currencySymbol),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed,
                                    maxLines = 1
                                )
                            }
                        }

                        // Remaining
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = if (summary.budgetRemaining >= 0) IncomeGreen.copy(alpha = 0.1f) else ExpenseRed.copy(alpha = 0.1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "বাকি আছে", fontSize = 12.sp, color = if (summary.budgetRemaining >= 0) IncomeGreen else ExpenseRed)
                                Text(
                                    text = CurrencyUtils.formatAmount(summary.budgetRemaining.coerceAtLeast(0.0), currencySymbol),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (summary.budgetRemaining >= 0) IncomeGreen else ExpenseRed,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Warnings Section (MANDATED by Prompt)
        if (summary.isOverBudget) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("budget_exceeded_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ExpenseRed.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "সতর্কতা",
                            tint = ExpenseRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "সতর্কতা: আপনার মাসিক বাজেট অতিক্রম করেছে।",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = ExpenseRed
                            )
                            Text(
                                text = "আপনার বাজেট থেকে ${CurrencyUtils.formatAmount(summary.monthlyExpense - summary.monthlyBudget, currencySymbol)} অতিরিক্ত খরচ হয়েছে। অনুগ্রহ করে খরচ নিয়ন্ত্রণ করুন।",
                                style = MaterialTheme.typography.bodySmall,
                                color = ExpenseRed.copy(alpha = 0.85f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        } else if (summary.isNearBudgetWarning) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("budget_near_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "সতর্কবার্তা",
                            tint = WarningAmber,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "সতর্কবার্তা: বাজেট প্রায় শেষ!",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = WarningAmber
                            )
                            Text(
                                text = "আপনি বাজেটের ৮০% বা তার বেশি খরচ করেছেন। অবশিষ্ট আছে মাত্র ${CurrencyUtils.formatAmount(summary.budgetRemaining, currencySymbol)}।",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Daily Allowance Recommendation Card
        item {
            val daysInMonth = DateUtils.getDaysInCurrentMonth()
            val currentDay = DateUtils.getCurrentDayOfMonth()
            val remainingDays = (daysInMonth - currentDay).coerceAtLeast(1)
            val recommendedDaily = if (summary.budgetRemaining > 0) summary.budgetRemaining / remainingDays else 0.0

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "দৈনিক ব্যয়ের পরামর্শ",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (summary.budgetRemaining > 0) {
                                "মাসের বাকি ${DateUtils.toBengaliDigits(remainingDays.toString())} দিনে আপনি প্রতিদিন সর্বোচ্চ ${CurrencyUtils.formatAmount(recommendedDaily, currencySymbol)} পর্যন্ত খরচ করতে পারবেন।"
                            } else {
                                "বাজেটের অতিরিক্ত খরচ বন্ধ রাখতে অপ্রয়োজনীয় কেনাকাটা এড়িয়ে চলুন।"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // Edit Budget Dialog
    if (isEditBudgetDialogOpen) {
        var dialogError by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { isEditBudgetDialogOpen = false },
            title = {
                Text(
                    text = "মাসিক বাজেট নির্ধারণ করুন",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "প্রতি মাসের শুরুতে আপনি কত টাকা খরচের লক্ষ্য রাখতে চান তা লিখুন:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = budgetInputText,
                        onValueChange = { input ->
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
                                budgetInputText = converted
                                dialogError = null
                            }
                        },
                        label = { Text("বাজেটের পরিমাণ ($currencySymbol)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("budget_input_field")
                    )

                    if (dialogError != null) {
                        Text(
                            text = dialogError!!,
                            color = ExpenseRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newBudget = budgetInputText.toDoubleOrNull()
                        if (newBudget == null || newBudget <= 0) {
                            dialogError = "সঠিক পরিমাণ লিখুন"
                            return@Button
                        }
                        onUpdateBudget(newBudget)
                        isEditBudgetDialogOpen = false
                    },
                    modifier = Modifier.testTag("save_budget_button")
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { isEditBudgetDialogOpen = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
