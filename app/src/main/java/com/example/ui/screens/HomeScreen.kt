package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransactionEntity
import com.example.model.DashboardSummary
import com.example.model.TransactionType
import com.example.ui.components.MainHeroBalanceCard
import com.example.ui.components.MonthlySummaryCard
import com.example.ui.components.TodaySummaryCard
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.EmeraldPrimary

@Composable
fun HomeScreen(
    summary: DashboardSummary,
    recentTransactions: List<TransactionEntity>,
    currencySymbol: String = "৳",
    onAddTransaction: (TransactionType) -> Unit,
    onEditTransaction: (TransactionEntity) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    onNavigateToTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header / Greeting
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "আমার দৈনিক খরচ",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "দৈনিক আয় ও ব্যয়ের পূর্ণাঙ্গ হিসাব",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Hero Balance Card
        item {
            MainHeroBalanceCard(
                summary = summary,
                currencySymbol = currencySymbol
            )
        }

        // Large "+ নতুন হিসাব যোগ করুন" Button (MANDATED)
        item {
            Button(
                onClick = { onAddTransaction(TransactionType.EXPENSE) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("add_new_transaction_large_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "+ নতুন হিসাব যোগ করুন",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                )
            }
        }

        // Today's Summary Card
        item {
            TodaySummaryCard(
                summary = summary,
                currencySymbol = currencySymbol
            )
        }

        // Monthly Summary Card with Budget warning
        item {
            MonthlySummaryCard(
                summary = summary,
                currencySymbol = currencySymbol
            )
        }

        // Recent Transactions Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সাম্প্রতিক হিসাবসমূহ",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onNavigateToTransactions() }
                        .padding(4.dp)
                        .testTag("view_all_transactions_button")
                ) {
                    Text(
                        text = "সব দেখুন",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Recent Transactions List (first 5)
        if (recentTransactions.isEmpty()) {
            item {
                Text(
                    text = "এখনও কোন হিসাব যুক্ত করা হয়নি। নিচের বাটনে চাপ দিয়ে প্রথম হিসাব যোগ করুন।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(recentTransactions.take(5), key = { it.id }) { tx ->
                TransactionItemCard(
                    transaction = tx,
                    currencySymbol = currencySymbol,
                    onEdit = { onEditTransaction(tx) },
                    onDelete = { onDeleteTransaction(tx) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
