package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val expenseCategories: Flow<List<CategoryEntity>> = categoryDao.getCategoriesByType("EXPENSE")
    val incomeCategories: Flow<List<CategoryEntity>> = categoryDao.getCategoriesByType("INCOME")

    val monthlyBudget: Flow<Double> = settingsDao.getSetting("monthly_budget").map { value ->
        value?.toDoubleOrNull() ?: 50000.0
    }

    val currencySymbol: Flow<String> = settingsDao.getSetting("currency").map { value ->
        value ?: "৳"
    }

    val themeMode: Flow<String> = settingsDao.getSetting("theme").map { value ->
        value ?: "SYSTEM"
    }

    val languageMode: Flow<String> = settingsDao.getSetting("language").map { value ->
        value ?: "BN"
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun clearAllTransactions() {
        transactionDao.clearAllTransactions()
    }

    suspend fun insertAllTransactions(transactions: List<TransactionEntity>) {
        transactionDao.insertAll(transactions)
    }

    suspend fun insertCategory(category: CategoryEntity): Long {
        return categoryDao.insertCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }

    suspend fun setMonthlyBudget(budget: Double) {
        settingsDao.saveSetting(AppSettingsEntity("monthly_budget", budget.toString()))
    }

    suspend fun setCurrency(currency: String) {
        settingsDao.saveSetting(AppSettingsEntity("currency", currency))
    }

    suspend fun setTheme(theme: String) {
        settingsDao.saveSetting(AppSettingsEntity("theme", theme))
    }

    suspend fun setLanguage(language: String) {
        settingsDao.saveSetting(AppSettingsEntity("language", language))
    }

    suspend fun resetAllData() {
        transactionDao.clearAllTransactions()
        categoryDao.clearAllCategories()
        categoryDao.insertAll(ExpenseDatabase.defaultExpenseCategories)
        categoryDao.insertAll(ExpenseDatabase.defaultIncomeCategories)
        settingsDao.saveSetting(AppSettingsEntity("monthly_budget", "50000"))
    }
}
