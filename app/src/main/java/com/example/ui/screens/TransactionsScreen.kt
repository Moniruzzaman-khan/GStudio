package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CategoryEntity
import com.example.data.TransactionEntity
import com.example.model.DateFilter
import com.example.model.TypeFilter
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.utils.CurrencyUtils
import com.example.utils.DateUtils
import java.util.Calendar

@Composable
fun TransactionsScreen(
    transactions: List<TransactionEntity>,
    categories: List<CategoryEntity>,
    currencySymbol: String = "৳",
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedDateFilter: DateFilter,
    onDateFilterChange: (DateFilter) -> Unit,
    selectedTypeFilter: TypeFilter,
    onTypeFilterChange: (TypeFilter) -> Unit,
    selectedCategoryFilter: String?,
    onCategoryFilterChange: (String?) -> Unit,
    onCustomDateRangeSet: (start: Long, end: Long) -> Unit,
    onEditTransaction: (TransactionEntity) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isCategoryDropdownOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("transactions_screen")
    ) {
        // Top Search Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_transactions_input"),
                placeholder = { Text("হিসাব খুঁজুন (খাত, নোট বা পরিমাণ)...", fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "খুঁজুন",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "পরিষ্কার করুন")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Date Filters Horizontal Scroll Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateFilter.values().forEach { filter ->
                    val isSelected = selectedDateFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (filter == DateFilter.CUSTOM) {
                                // Show custom date picker dialog
                                val cal = Calendar.getInstance()
                                DatePickerDialog(context, { _, y, m, d ->
                                    val startCal = Calendar.getInstance().apply {
                                        set(y, m, d, 0, 0, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    val endCal = Calendar.getInstance().apply {
                                        set(y, m, d, 23, 59, 59)
                                        set(Calendar.MILLISECOND, 999)
                                    }
                                    onCustomDateRangeSet(startCal.timeInMillis, endCal.timeInMillis)
                                    onDateFilterChange(DateFilter.CUSTOM)
                                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            } else {
                                onDateFilterChange(filter)
                            }
                        },
                        label = { Text(filter.labelBn, fontSize = 12.sp) },
                        leadingIcon = if (filter == DateFilter.CUSTOM) {
                            { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Type Filter & Category Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type filters
                TypeFilter.values().forEach { filter ->
                    val isSelected = selectedTypeFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTypeFilterChange(filter) },
                        label = { Text(filter.labelBn, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (filter) {
                                TypeFilter.EXPENSE_ONLY -> ExpenseRed
                                TypeFilter.INCOME_ONLY -> IncomeGreen
                                else -> MaterialTheme.colorScheme.secondary
                            },
                            selectedLabelColor = Color.White
                        )
                    )
                }

                // Category Filter Dropdown
                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedCategoryFilter != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isCategoryDropdownOpen = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (selectedCategoryFilter != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedCategoryFilter ?: "সকল ক্যাটাগরি",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedCategoryFilter != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = isCategoryDropdownOpen,
                        onDismissRequest = { isCategoryDropdownOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("সকল ক্যাটাগরি") },
                            onClick = {
                                onCategoryFilterChange(null)
                                isCategoryDropdownOpen = false
                            }
                        )
                        categories.map { it.name }.distinct().forEach { catName ->
                            DropdownMenuItem(
                                text = { Text(catName) },
                                onClick = {
                                    onCategoryFilterChange(catName)
                                    isCategoryDropdownOpen = false
                                }
                            )
                        }
                    }
                }

                // Clear filter chip if any active
                if (selectedCategoryFilter != null || selectedTypeFilter != TypeFilter.ALL || selectedDateFilter != DateFilter.ALL) {
                    Text(
                        text = "রিসেট",
                        color = ExpenseRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                onDateFilterChange(DateFilter.ALL)
                                onTypeFilterChange(TypeFilter.ALL)
                                onCategoryFilterChange(null)
                                onSearchQueryChange("")
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Summary bar for current filtered list
        val filteredExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        val filteredIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "মোট লেনদেন: ${DateUtils.toBengaliDigits(transactions.size.toString())} টি",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (filteredIncome > 0) {
                    Text(
                        text = "+${CurrencyUtils.formatAmount(filteredIncome, currencySymbol)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = IncomeGreen
                    )
                }
                if (filteredExpense > 0) {
                    Text(
                        text = "-${CurrencyUtils.formatAmount(filteredExpense, currencySymbol)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = ExpenseRed
                    )
                }
            }
        }

        // Transactions List
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "কোন হিসাব পাওয়া যায়নি",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "ফিল্টার পরিবর্তন করুন অথবা নতুন হিসাব যুক্ত করুন",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(transactions, key = { it.id }) { tx ->
                    TransactionItemCard(
                        transaction = tx,
                        currencySymbol = currencySymbol,
                        onEdit = { onEditTransaction(tx) },
                        onDelete = { onDeleteTransaction(tx) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}
