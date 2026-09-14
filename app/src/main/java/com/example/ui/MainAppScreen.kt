package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransactionType
import com.example.ui.components.AddCategoryDialog
import com.example.ui.components.AddEditTransactionSheet
import com.example.ui.components.CategoriesManageSheet
import com.example.ui.components.ClearAllDataConfirmDialog
import com.example.ui.components.DeleteTransactionConfirmDialog
import com.example.ui.navigation.NavDestination
import com.example.ui.screens.BudgetScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.viewmodel.ExpenseViewModel

@Composable
fun MainAppScreen(
    viewModel: ExpenseViewModel
) {
    val context = LocalContext.current
    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }

    val summary by viewModel.dashboardSummary.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()
    val allCategories by viewModel.allCategories.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val currentTheme by viewModel.themeMode.collectAsState()
    val currentLanguage by viewModel.languageMode.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDateFilter by viewModel.selectedDateFilter.collectAsState()
    val selectedTypeFilter by viewModel.selectedTypeFilter.collectAsState()
    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsState()

    val isAddEditSheetOpen by viewModel.isAddEditSheetOpen.collectAsState()
    val editingTransaction by viewModel.editingTransaction.collectAsState()
    val initialTypeForAdd by viewModel.initialTypeForAdd.collectAsState()
    val transactionToDelete by viewModel.transactionToDelete.collectAsState()
    val isClearAllDialogOpen by viewModel.isClearAllDialogOpen.collectAsState()
    val isAddCustomCategoryDialogOpen by viewModel.isCustomCategoryDialogOpen.collectAsState()

    var isManageCategoriesSheetOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.labelBn,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = destination.labelBn,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag(destination.testTag)
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentDestination != NavDestination.SETTINGS) {
                FloatingActionButton(
                    onClick = { viewModel.openAddSheet(TransactionType.EXPENSE) },
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("main_fab_add")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "হিসাব যোগ করুন",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentDestination,
            modifier = Modifier.padding(innerPadding),
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                NavDestination.HOME -> HomeScreen(
                    summary = summary,
                    recentTransactions = allTransactions,
                    currencySymbol = currencySymbol,
                    onAddTransaction = { type -> viewModel.openAddSheet(type) },
                    onEditTransaction = { tx -> viewModel.openEditSheet(tx) },
                    onDeleteTransaction = { tx -> viewModel.confirmDeleteTransaction(tx) },
                    onNavigateToTransactions = { currentDestination = NavDestination.TRANSACTIONS }
                )

                NavDestination.TRANSACTIONS -> TransactionsScreen(
                    transactions = filteredTransactions,
                    categories = allCategories,
                    currencySymbol = currencySymbol,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.searchQuery.value = it },
                    selectedDateFilter = selectedDateFilter,
                    onDateFilterChange = { viewModel.selectedDateFilter.value = it },
                    selectedTypeFilter = selectedTypeFilter,
                    onTypeFilterChange = { viewModel.selectedTypeFilter.value = it },
                    selectedCategoryFilter = selectedCategoryFilter,
                    onCategoryFilterChange = { viewModel.selectedCategoryFilter.value = it },
                    onCustomDateRangeSet = { start, end ->
                        viewModel.customStartDate.value = start
                        viewModel.customEndDate.value = end
                    },
                    onEditTransaction = { tx -> viewModel.openEditSheet(tx) },
                    onDeleteTransaction = { tx -> viewModel.confirmDeleteTransaction(tx) }
                )

                NavDestination.REPORTS -> ReportsScreen(
                    summary = summary,
                    currencySymbol = currencySymbol
                )

                NavDestination.BUDGET -> BudgetScreen(
                    summary = summary,
                    currencySymbol = currencySymbol,
                    onUpdateBudget = { newBudget -> viewModel.updateMonthlyBudget(newBudget) }
                )

                NavDestination.SETTINGS -> SettingsScreen(
                    currentCurrency = currencySymbol,
                    onCurrencyChange = { sym -> viewModel.updateCurrency(sym) },
                    currentTheme = currentTheme,
                    onThemeChange = { mode -> viewModel.updateTheme(mode) },
                    currentLanguage = currentLanguage,
                    onLanguageChange = { lang -> viewModel.updateLanguage(lang) },
                    monthlyBudget = summary.monthlyBudget,
                    onUpdateBudget = { currentDestination = NavDestination.BUDGET },
                    onManageCategories = { isManageCategoriesSheetOpen = true },
                    onExportCsv = { viewModel.exportTransactions(context, "CSV") },
                    onExportJson = { viewModel.exportTransactions(context, "JSON") },
                    onImportData = { content, format, onResult ->
                        viewModel.importTransactions(content, format, onResult)
                    },
                    onClearAllData = { viewModel.openClearAllDialog() }
                )
            }
        }
    }

    // Modal Bottom Sheet: Add or Edit Transaction
    if (isAddEditSheetOpen) {
        AddEditTransactionSheet(
            initialTransaction = editingTransaction,
            initialType = initialTypeForAdd,
            categories = allCategories,
            currencySymbol = currencySymbol,
            onSave = { id, type, category, amount, date, time, timestamp, note ->
                viewModel.saveTransaction(id, type, category, amount, date, time, timestamp, note)
            },
            onAddCustomCategory = { viewModel.isCustomCategoryDialogOpen.value = true },
            onDismiss = { viewModel.closeAddEditSheet() }
        )
    }

    // Delete Confirmation Dialog
    if (transactionToDelete != null) {
        DeleteTransactionConfirmDialog(
            onConfirm = { viewModel.deleteConfirmedTransaction() },
            onDismiss = { viewModel.dismissDeleteDialog() }
        )
    }

    // Clear All Data Confirmation Dialog
    if (isClearAllDialogOpen) {
        ClearAllDataConfirmDialog(
            onConfirm = { viewModel.clearAllDataConfirmed() },
            onDismiss = { viewModel.dismissClearAllDialog() }
        )
    }

    // Add Custom Category Dialog
    if (isAddCustomCategoryDialogOpen) {
        AddCategoryDialog(
            onAdd = { name, type, iconName ->
                viewModel.addCustomCategory(name, type, iconName)
            },
            onDismiss = { viewModel.isCustomCategoryDialogOpen.value = false }
        )
    }

    // Manage Categories Sheet
    if (isManageCategoriesSheetOpen) {
        CategoriesManageSheet(
            categories = allCategories,
            onAddCustomCategory = {
                viewModel.isCustomCategoryDialogOpen.value = true
            },
            onDeleteCategory = { cat -> viewModel.deleteCategory(cat) },
            onDismiss = { isManageCategoriesSheetOpen = false }
        )
    }
}
