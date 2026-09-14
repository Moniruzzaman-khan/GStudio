package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DashboardSummary
import com.example.ui.components.CategoryDonutChart
import com.example.ui.components.DailyExpenseBarChart
import com.example.ui.components.IncomeVsExpenseComparisonCard
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.utils.CurrencyUtils
import com.example.utils.DateUtils

@Composable
fun ReportsScreen(
    summary: DashboardSummary,
    currencySymbol: String = "৳",
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reports_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Title
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "আর্থিক রিপোর্ট ও বিশ্লেষণ",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${DateUtils.getCurrentMonthNameBn()} মাসের বিস্তারিত খরচের পর্যালোচনা",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Highest Spending Category Card (Highlight mandated by prompt!)
        if (!summary.highestExpenseCategory.isNullOrBlank()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("highest_spending_category_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF7ED) // Warm peach highlight
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEA580C).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFEA580C),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "এই মাসে আপনার সবচেয়ে বেশি খরচ হয়েছে:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                color = Color(0xFF7C2D12)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = summary.highestExpenseCategory,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 19.sp
                                    ),
                                    color = Color(0xFF9A3412)
                                )
                                Text(
                                    text = CurrencyUtils.formatAmount(summary.highestExpenseAmount, currencySymbol),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFFEA580C)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1. Income vs Expense Comparison Card
        item {
            IncomeVsExpenseComparisonCard(
                income = summary.monthlyIncome,
                expense = summary.monthlyExpense,
                savings = summary.monthlySavings,
                currencySymbol = currencySymbol
            )
        }

        // 2. Category-wise Expenses Donut Chart (Mandated)
        item {
            CategoryDonutChart(
                items = summary.categoryExpensesThisMonth,
                totalExpense = summary.monthlyExpense,
                currencySymbol = currencySymbol
            )
        }

        // 3. Daily Expenses Bar Chart (Mandated)
        item {
            DailyExpenseBarChart(
                items = summary.dailyExpensesThisMonth,
                currencySymbol = currencySymbol
            )
        }

        // 4. Monthly Key Metrics Grid
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("monthly_metrics_grid_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "মাসিক সংক্ষিপ্ত পরিসংখ্যান",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    MetricRow(
                        label = "এই মাসের মোট আয়",
                        value = CurrencyUtils.formatAmount(summary.monthlyIncome, currencySymbol),
                        color = IncomeGreen,
                        icon = Icons.Default.ArrowDownward
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    MetricRow(
                        label = "এই মাসের মোট খরচ",
                        value = CurrencyUtils.formatAmount(summary.monthlyExpense, currencySymbol),
                        color = ExpenseRed,
                        icon = Icons.Default.ArrowUpward
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    MetricRow(
                        label = "এই মাসের মোট সঞ্চয়",
                        value = CurrencyUtils.formatAmount(summary.monthlySavings, currencySymbol),
                        color = if (summary.monthlySavings >= 0) IncomeGreen else ExpenseRed,
                        icon = Icons.Default.Savings
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val currentDay = DateUtils.getCurrentDayOfMonth()
                    val dailyAvg = if (currentDay > 0) summary.monthlyExpense / currentDay else 0.0
                    MetricRow(
                        label = "দৈনিক গড় খরচ",
                        value = CurrencyUtils.formatAmount(dailyAvg, currencySymbol),
                        color = MaterialTheme.colorScheme.primary,
                        icon = Icons.Default.CalendarMonth
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}
