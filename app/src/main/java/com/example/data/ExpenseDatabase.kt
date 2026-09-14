package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: ExpenseDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "my_daily_expense.db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        val defaultExpenseCategories = listOf(
            CategoryEntity(name = "খাবার", type = "EXPENSE", iconName = "restaurant", isDefault = true),
            CategoryEntity(name = "বাজার", type = "EXPENSE", iconName = "shopping_cart", isDefault = true),
            CategoryEntity(name = "যাতায়াত", type = "EXPENSE", iconName = "commute", isDefault = true),
            CategoryEntity(name = "বাসা ভাড়া", type = "EXPENSE", iconName = "home", isDefault = true),
            CategoryEntity(name = "বিদ্যুৎ বিল", type = "EXPENSE", iconName = "bolt", isDefault = true),
            CategoryEntity(name = "পানি বিল", type = "EXPENSE", iconName = "water_drop", isDefault = true),
            CategoryEntity(name = "গ্যাস বিল", type = "EXPENSE", iconName = "local_gas_station", isDefault = true),
            CategoryEntity(name = "মোবাইল/ইন্টারনেট", type = "EXPENSE", iconName = "phone_android", isDefault = true),
            CategoryEntity(name = "চিকিৎসা", type = "EXPENSE", iconName = "medical_services", isDefault = true),
            CategoryEntity(name = "শিক্ষা", type = "EXPENSE", iconName = "school", isDefault = true),
            CategoryEntity(name = "কেনাকাটা", type = "EXPENSE", iconName = "shopping_bag", isDefault = true),
            CategoryEntity(name = "পরিবার", type = "EXPENSE", iconName = "family_restroom", isDefault = true),
            CategoryEntity(name = "বিনোদন", type = "EXPENSE", iconName = "movie", isDefault = true),
            CategoryEntity(name = "অন্যান্য", type = "EXPENSE", iconName = "category", isDefault = true)
        )

        val defaultIncomeCategories = listOf(
            CategoryEntity(name = "বেতন", type = "INCOME", iconName = "account_balance_wallet", isDefault = true),
            CategoryEntity(name = "ব্যবসা", type = "INCOME", iconName = "store", isDefault = true),
            CategoryEntity(name = "ফ্রিল্যান্স", type = "INCOME", iconName = "laptop", isDefault = true),
            CategoryEntity(name = "বোনাস", type = "INCOME", iconName = "card_giftcard", isDefault = true),
            CategoryEntity(name = "অন্যান্য", type = "INCOME", iconName = "payments", isDefault = true)
        )
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }
    }
}

suspend fun populateInitialData(database: ExpenseDatabase) {
    val categoryDao = database.categoryDao()
    if (categoryDao.getCategoryCount() == 0) {
        categoryDao.insertAll(ExpenseDatabase.defaultExpenseCategories)
        categoryDao.insertAll(ExpenseDatabase.defaultIncomeCategories)
    }
    val settingsDao = database.settingsDao()
    if (settingsDao.getSettingDirect("monthly_budget") == null) {
        settingsDao.saveSetting(AppSettingsEntity("monthly_budget", "50000"))
    }
    if (settingsDao.getSettingDirect("currency") == null) {
        settingsDao.saveSetting(AppSettingsEntity("currency", "৳"))
    }
    if (settingsDao.getSettingDirect("theme") == null) {
        settingsDao.saveSetting(AppSettingsEntity("theme", "SYSTEM"))
    }
    if (settingsDao.getSettingDirect("language") == null) {
        settingsDao.saveSetting(AppSettingsEntity("language", "BN"))
    }
}
