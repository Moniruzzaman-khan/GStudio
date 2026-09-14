package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryHelper {
    fun getCategoryIcon(iconName: String, categoryName: String): ImageVector {
        return when (iconName.lowercase()) {
            "restaurant", "food" -> Icons.Default.Restaurant
            "shopping_cart", "groceries" -> Icons.Default.ShoppingCart
            "commute", "transport" -> Icons.Default.Commute
            "home", "rent" -> Icons.Default.Home
            "bolt", "electricity" -> Icons.Default.Bolt
            "water_drop", "water" -> Icons.Default.WaterDrop
            "local_gas_station", "gas" -> Icons.Default.LocalGasStation
            "phone_android", "internet" -> Icons.Default.PhoneAndroid
            "medical_services", "health" -> Icons.Default.MedicalServices
            "school", "education" -> Icons.Default.School
            "shopping_bag", "shopping" -> Icons.Default.ShoppingBag
            "family_restroom", "people", "family" -> Icons.Default.People
            "movie", "entertainment" -> Icons.Default.Movie
            "account_balance_wallet", "salary" -> Icons.Default.AccountBalanceWallet
            "store", "business" -> Icons.Default.Store
            "laptop", "freelance" -> Icons.Default.Laptop
            "card_giftcard", "bonus" -> Icons.Default.CardGiftcard
            "payments" -> Icons.Default.Payments
            "attach_money" -> Icons.Default.AttachMoney
            else -> {
                // Fallback based on category name in Bengali
                when {
                    categoryName.contains("খাবার") -> Icons.Default.Restaurant
                    categoryName.contains("বাজার") -> Icons.Default.ShoppingCart
                    categoryName.contains("যাতায়াত") -> Icons.Default.Commute
                    categoryName.contains("ভাড়া") -> Icons.Default.Home
                    categoryName.contains("বিদ্যুৎ") -> Icons.Default.Bolt
                    categoryName.contains("পানি") -> Icons.Default.WaterDrop
                    categoryName.contains("গ্যাস") -> Icons.Default.LocalGasStation
                    categoryName.contains("মোবাইল") || categoryName.contains("নেট") -> Icons.Default.PhoneAndroid
                    categoryName.contains("চিকিৎসা") -> Icons.Default.MedicalServices
                    categoryName.contains("শিক্ষা") -> Icons.Default.School
                    categoryName.contains("কেনাকাটা") -> Icons.Default.ShoppingBag
                    categoryName.contains("পরিবার") -> Icons.Default.People
                    categoryName.contains("বিনোদন") -> Icons.Default.Movie
                    categoryName.contains("বেতন") -> Icons.Default.AccountBalanceWallet
                    categoryName.contains("ব্যবসা") -> Icons.Default.Store
                    categoryName.contains("ফ্রিল্যান্স") -> Icons.Default.Laptop
                    categoryName.contains("বোনাস") -> Icons.Default.CardGiftcard
                    else -> Icons.Default.Category
                }
            }
        }
    }

    fun getCategoryColor(categoryName: String, isExpense: Boolean): Color {
        return if (isExpense) {
            when {
                categoryName.contains("খাবার") -> Color(0xFFE65100) // Deep Orange
                categoryName.contains("বাজার") -> Color(0xFF2E7D32) // Forest Green
                categoryName.contains("যাতায়াত") -> Color(0xFF0288D1) // Light Blue
                categoryName.contains("বাসা") || categoryName.contains("ভাড়া") -> Color(0xFF5E35B1) // Deep Purple
                categoryName.contains("বিদ্যুৎ") -> Color(0xFFF57F17) // Amber/Yellow
                categoryName.contains("পানি") -> Color(0xFF0097A7) // Cyan
                categoryName.contains("গ্যাস") -> Color(0xFFD84315) // Rust Orange
                categoryName.contains("মোবাইল") || categoryName.contains("নেট") -> Color(0xFF3949AB) // Indigo
                categoryName.contains("চিকিৎসা") -> Color(0xFFC2185B) // Pink/Crimson
                categoryName.contains("শিক্ষা") -> Color(0xFF00897B) // Teal
                categoryName.contains("কেনাকাটা") -> Color(0xFF8E24AA) // Purple
                categoryName.contains("পরিবার") -> Color(0xFF43A047) // Green
                categoryName.contains("বিনোদন") -> Color(0xFFD81B60) // Rose
                else -> Color(0xFF546E7A) // Blue Grey
            }
        } else {
            when {
                categoryName.contains("বেতন") -> Color(0xFF10B981) // Emerald
                categoryName.contains("ব্যবসা") -> Color(0xFF059669) // Green
                categoryName.contains("ফ্রিল্যান্স") -> Color(0xFF0D9488) // Teal
                categoryName.contains("বোনাস") -> Color(0xFFF59E0B) // Gold
                else -> Color(0xFF14B8A6) // Cyan/Teal
            }
        }
    }
}
