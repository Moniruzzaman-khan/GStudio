package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ExpenseDatabase
import com.example.data.ExpenseRepository
import com.example.ui.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ExpenseViewModel
import com.example.viewmodel.ExpenseViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = ExpenseDatabase.getDatabase(applicationContext)
        val repository = ExpenseRepository(
            transactionDao = database.transactionDao(),
            categoryDao = database.categoryDao(),
            settingsDao = database.settingsDao()
        )
        val factory = ExpenseViewModelFactory(repository)

        setContent {
            val expenseViewModel: ExpenseViewModel = viewModel(factory = factory)
            val themeMode by expenseViewModel.themeMode.collectAsState()

            val isDarkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                MainAppScreen(viewModel = expenseViewModel)
            }
        }
    }
}
