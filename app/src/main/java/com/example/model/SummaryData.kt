package com.example.model

data class DailyExpenseItem(
    val dayNumber: Int,
    val dayLabel: String,
    val amount: Double
)

data class CategoryExpenseItem(
    val category: String,
    val amount: Double,
    val percentage: Float
)

data class DashboardSummary(
    val todayExpense: Double = 0.0,
    val todayIncome: Double = 0.0,
    val todayBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val monthlySavings: Double = 0.0,
    val monthlyBudget: Double = 50000.0,
    val budgetRemaining: Double = 50000.0,
    val budgetUsedPercent: Float = 0f,
    val isNearBudgetWarning: Boolean = false,
    val isOverBudget: Boolean = false,
    val totalBalance: Double = 0.0,
    val highestExpenseCategory: String? = null,
    val highestExpenseAmount: Double = 0.0,
    val dailyExpensesThisMonth: List<DailyExpenseItem> = emptyList(),
    val categoryExpensesThisMonth: List<CategoryExpenseItem> = emptyList()
)
