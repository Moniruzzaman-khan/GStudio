package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CategoryEntity
import com.example.data.ExpenseRepository
import com.example.data.TransactionEntity
import com.example.model.CategoryExpenseItem
import com.example.model.DailyExpenseItem
import com.example.model.DashboardSummary
import com.example.model.DateFilter
import com.example.model.TransactionType
import com.example.model.TypeFilter
import com.example.utils.DateUtils
import com.example.utils.ExportImportUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseCategories: StateFlow<List<CategoryEntity>> = repository.expenseCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomeCategories: StateFlow<List<CategoryEntity>> = repository.incomeCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthlyBudget: StateFlow<Double> = repository.monthlyBudget
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 50000.0)

    val currencySymbol: StateFlow<String> = repository.currencySymbol
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "৳")

    val themeMode: StateFlow<String> = repository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val languageMode: StateFlow<String> = repository.languageMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "BN")

    // Filter states
    val searchQuery = MutableStateFlow("")
    val selectedDateFilter = MutableStateFlow(DateFilter.ALL)
    val selectedTypeFilter = MutableStateFlow(TypeFilter.ALL)
    val selectedCategoryFilter = MutableStateFlow<String?>(null)
    val customStartDate = MutableStateFlow<Long?>(null)
    val customEndDate = MutableStateFlow<Long?>(null)

    // UI state for Dialogs and Sheets
    val isAddEditSheetOpen = MutableStateFlow(false)
    val editingTransaction = MutableStateFlow<TransactionEntity?>(null)
    val initialTypeForAdd = MutableStateFlow(TransactionType.EXPENSE)
    val transactionToDelete = MutableStateFlow<TransactionEntity?>(null)
    val isClearAllDialogOpen = MutableStateFlow(false)
    val isCustomCategoryDialogOpen = MutableStateFlow(false)

    // Reactive Dashboard Summary
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        allTransactions,
        monthlyBudget
    ) { transactions, budget ->
        computeDashboardSummary(transactions, budget)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    // Reactive Filtered Transactions
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        searchQuery,
        selectedDateFilter,
        selectedTypeFilter,
        selectedCategoryFilter
    ) { transactions, query, dateFilter, typeFilter, categoryFilter ->
        filterTransactions(
            transactions = transactions,
            query = query,
            dateFilter = dateFilter,
            typeFilter = typeFilter,
            categoryFilter = categoryFilter,
            customStart = customStartDate.value,
            customEnd = customEndDate.value
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun computeDashboardSummary(
        transactions: List<TransactionEntity>,
        budget: Double
    ): DashboardSummary {
        val startOfToday = DateUtils.getStartOfToday()
        val endOfToday = DateUtils.getEndOfToday()
        val startOfMonth = DateUtils.getStartOfMonth()
        val endOfMonth = DateUtils.getEndOfMonth()

        var todayExpense = 0.0
        var todayIncome = 0.0
        var monthlyExpense = 0.0
        var monthlyIncome = 0.0
        var totalIncome = 0.0
        var totalExpense = 0.0

        val categoryExpenseMap = mutableMapOf<String, Double>()
        val dailyExpenseMap = mutableMapOf<Int, Double>()

        for (tx in transactions) {
            val isExpense = tx.type == "EXPENSE"
            val isIncome = tx.type == "INCOME"

            if (isExpense) totalExpense += tx.amount
            if (isIncome) totalIncome += tx.amount

            // Today's summary
            if (tx.timestamp in startOfToday..endOfToday) {
                if (isExpense) todayExpense += tx.amount
                if (isIncome) todayIncome += tx.amount
            }

            // Monthly summary
            if (tx.timestamp in startOfMonth..endOfMonth) {
                if (isExpense) {
                    monthlyExpense += tx.amount
                    categoryExpenseMap[tx.category] = (categoryExpenseMap[tx.category] ?: 0.0) + tx.amount

                    // Parse day for daily chart
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = tx.timestamp
                    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
                    dailyExpenseMap[dayOfMonth] = (dailyExpenseMap[dayOfMonth] ?: 0.0) + tx.amount
                }
                if (isIncome) {
                    monthlyIncome += tx.amount
                }
            }
        }

        val todayBalance = todayIncome - todayExpense
        val monthlySavings = monthlyIncome - monthlyExpense
        val totalBalance = totalIncome - totalExpense
        val budgetRemaining = budget - monthlyExpense
        val budgetUsedPercent = if (budget > 0) ((monthlyExpense / budget) * 100).toFloat() else 0f
        val isOverBudget = monthlyExpense > budget
        val isNearBudgetWarning = budgetUsedPercent >= 80f && !isOverBudget

        // Highest spending category
        var highestCat: String? = null
        var highestAmount = 0.0
        for ((cat, amt) in categoryExpenseMap) {
            if (amt > highestAmount) {
                highestAmount = amt
                highestCat = cat
            }
        }

        // Category breakdown items
        val categoryItems = categoryExpenseMap.map { (cat, amt) ->
            val pct = if (monthlyExpense > 0) ((amt / monthlyExpense) * 100).toFloat() else 0f
            CategoryExpenseItem(cat, amt, pct)
        }.sortedByDescending { it.amount }

        // Daily breakdown items for current month
        val currentDay = DateUtils.getCurrentDayOfMonth()
        val dailyItems = (1..currentDay).map { day ->
            val amt = dailyExpenseMap[day] ?: 0.0
            DailyExpenseItem(
                dayNumber = day,
                dayLabel = DateUtils.toBengaliDigits(day.toString()),
                amount = amt
            )
        }

        return DashboardSummary(
            todayExpense = todayExpense,
            todayIncome = todayIncome,
            todayBalance = todayBalance,
            monthlyIncome = monthlyIncome,
            monthlyExpense = monthlyExpense,
            monthlySavings = monthlySavings,
            monthlyBudget = budget,
            budgetRemaining = budgetRemaining,
            budgetUsedPercent = budgetUsedPercent,
            isNearBudgetWarning = isNearBudgetWarning,
            isOverBudget = isOverBudget,
            totalBalance = totalBalance,
            highestExpenseCategory = highestCat,
            highestExpenseAmount = highestAmount,
            dailyExpensesThisMonth = dailyItems,
            categoryExpensesThisMonth = categoryItems
        )
    }

    private fun filterTransactions(
        transactions: List<TransactionEntity>,
        query: String,
        dateFilter: DateFilter,
        typeFilter: TypeFilter,
        categoryFilter: String?,
        customStart: Long?,
        customEnd: Long?
    ): List<TransactionEntity> {
        val now = System.currentTimeMillis()
        val startOfToday = DateUtils.getStartOfToday()
        val endOfToday = DateUtils.getEndOfToday()
        val startOfYesterday = DateUtils.getStartOfYesterday()
        val endOfYesterday = DateUtils.getEndOfYesterday()
        val startOfWeek = DateUtils.getStartOfWeek()
        val startOfMonth = DateUtils.getStartOfMonth()
        val endOfMonth = DateUtils.getEndOfMonth()
        val startOfLastMonth = DateUtils.getStartOfLastMonth()
        val endOfLastMonth = DateUtils.getEndOfLastMonth()

        return transactions.filter { tx ->
            // Search query filter (matches category name, note, or amount)
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val q = query.trim().lowercase()
                tx.category.lowercase().contains(q) ||
                tx.note.lowercase().contains(q) ||
                tx.amount.toString().contains(q) ||
                DateUtils.toBengaliDigits(tx.amount.toInt().toString()).contains(q)
            }

            // Type filter
            val matchesType = when (typeFilter) {
                TypeFilter.ALL -> true
                TypeFilter.EXPENSE_ONLY -> tx.type == "EXPENSE"
                TypeFilter.INCOME_ONLY -> tx.type == "INCOME"
            }

            // Category filter
            val matchesCategory = categoryFilter == null || tx.category == categoryFilter

            // Date filter
            val matchesDate = when (dateFilter) {
                DateFilter.ALL -> true
                DateFilter.TODAY -> tx.timestamp in startOfToday..endOfToday
                DateFilter.YESTERDAY -> tx.timestamp in startOfYesterday..endOfYesterday
                DateFilter.THIS_WEEK -> tx.timestamp in startOfWeek..now
                DateFilter.THIS_MONTH -> tx.timestamp in startOfMonth..endOfMonth
                DateFilter.LAST_MONTH -> tx.timestamp in startOfLastMonth..endOfLastMonth
                DateFilter.CUSTOM -> {
                    val s = customStart ?: 0L
                    val e = customEnd ?: Long.MAX_VALUE
                    tx.timestamp in s..e
                }
            }

            matchesQuery && matchesType && matchesCategory && matchesDate
        }
    }

    fun openAddSheet(type: TransactionType = TransactionType.EXPENSE) {
        editingTransaction.value = null
        initialTypeForAdd.value = type
        isAddEditSheetOpen.value = true
    }

    fun openEditSheet(transaction: TransactionEntity) {
        editingTransaction.value = transaction
        initialTypeForAdd.value = if (transaction.type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE
        isAddEditSheetOpen.value = true
    }

    fun closeAddEditSheet() {
        isAddEditSheetOpen.value = false
        editingTransaction.value = null
    }

    fun saveTransaction(
        id: Long,
        type: String,
        category: String,
        amount: Double,
        date: String,
        time: String,
        timestamp: Long,
        note: String
    ) {
        viewModelScope.launch {
            val entity = TransactionEntity(
                id = id,
                type = type,
                category = category,
                amount = amount,
                date = date,
                time = time,
                timestamp = timestamp,
                note = note.trim()
            )
            if (id == 0L) {
                repository.insertTransaction(entity)
            } else {
                repository.updateTransaction(entity)
            }
            closeAddEditSheet()
        }
    }

    fun confirmDeleteTransaction(transaction: TransactionEntity) {
        transactionToDelete.value = transaction
    }

    fun dismissDeleteDialog() {
        transactionToDelete.value = null
    }

    fun deleteConfirmedTransaction() {
        val tx = transactionToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteTransaction(tx)
            transactionToDelete.value = null
        }
    }

    fun openClearAllDialog() {
        isClearAllDialogOpen.value = true
    }

    fun dismissClearAllDialog() {
        isClearAllDialogOpen.value = false
    }

    fun clearAllDataConfirmed() {
        viewModelScope.launch {
            repository.resetAllData()
            isClearAllDialogOpen.value = false
        }
    }

    fun addCustomCategory(name: String, type: String, iconName: String = "category") {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertCategory(
                CategoryEntity(
                    name = name.trim(),
                    type = type,
                    iconName = iconName,
                    isDefault = false
                )
            )
            isCustomCategoryDialogOpen.value = false
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun updateMonthlyBudget(budget: Double) {
        viewModelScope.launch {
            repository.setMonthlyBudget(budget)
        }
    }

    fun updateCurrency(currency: String) {
        viewModelScope.launch {
            repository.setCurrency(currency)
        }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch {
            repository.setTheme(theme)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            repository.setLanguage(language)
        }
    }

    fun exportTransactions(context: Context, format: String) {
        val transactions = allTransactions.value
        if (format.equals("CSV", ignoreCase = true)) {
            val csv = ExportImportUtils.exportToCsv(transactions)
            val fileName = "amar_dainik_khoroch_${DateUtils.getCurrentDate()}.csv"
            ExportImportUtils.shareExportedFile(context, fileName, csv, "text/csv")
        } else {
            val json = ExportImportUtils.exportToJson(transactions)
            val fileName = "amar_dainik_khoroch_${DateUtils.getCurrentDate()}.json"
            ExportImportUtils.shareExportedFile(context, fileName, json, "application/json")
        }
    }

    fun importTransactions(content: String, format: String, onComplete: (Boolean, Int) -> Unit) {
        viewModelScope.launch {
            try {
                val transactions = if (format.equals("CSV", ignoreCase = true)) {
                    ExportImportUtils.parseCsv(content)
                } else {
                    ExportImportUtils.parseJson(content)
                }
                if (transactions.isNotEmpty()) {
                    repository.insertAllTransactions(transactions)
                    onComplete(true, transactions.size)
                } else {
                    onComplete(false, 0)
                }
            } catch (e: Exception) {
                onComplete(false, 0)
            }
        }
    }
}
