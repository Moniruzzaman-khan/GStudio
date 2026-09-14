package com.example.model

enum class DateFilter(val labelBn: String, val labelEn: String) {
    ALL("সব সময়", "All Time"),
    TODAY("আজ", "Today"),
    YESTERDAY("গতকাল", "Yesterday"),
    THIS_WEEK("এই সপ্তাহ", "This Week"),
    THIS_MONTH("এই মাস", "This Month"),
    LAST_MONTH("গত মাস", "Last Month"),
    CUSTOM("কাস্টম তারিখ", "Custom Range")
}

enum class TypeFilter(val labelBn: String, val labelEn: String) {
    ALL("সকল লেনদেন", "All"),
    EXPENSE_ONLY("শুধু খরচ", "Expense Only"),
    INCOME_ONLY("শুধু আয়", "Income Only")
}
