package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.utils.CurrencyUtils
import com.example.utils.ExportImportUtils

@Composable
fun SettingsScreen(
    currentCurrency: String,
    onCurrencyChange: (String) -> Unit,
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    monthlyBudget: Double,
    onUpdateBudget: () -> Unit,
    onManageCategories: () -> Unit,
    onExportCsv: () -> Unit,
    onExportJson: () -> Unit,
    onImportData: (content: String, format: String, onResult: (Boolean, Int) -> Unit) -> Unit,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var isCurrencyDialogOpen by remember { mutableStateOf(false) }
    var isThemeDialogOpen by remember { mutableStateOf(false) }
    var isLanguageDialogOpen by remember { mutableStateOf(false) }
    var isAboutDialogOpen by remember { mutableStateOf(false) }
    var isPasteImportDialogOpen by remember { mutableStateOf(false) }
    var pasteContent by remember { mutableStateOf("") }

    // File Picker for importing CSV or JSON
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val content = ExportImportUtils.readStream(inputStream)
                    val isJson = content.trim().startsWith("{") || content.trim().startsWith("[")
                    val format = if (isJson) "JSON" else "CSV"
                    onImportData(content, format) { success, count ->
                        if (success) {
                            Toast.makeText(context, "সফলভাবে $count টি হিসাব যুক্ত করা হয়েছে!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "ফাইল পড়তে সমস্যা হয়েছে। সঠিক ফাইল নির্বাচন করুন।", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "ত্রুটি: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "সেটিংস",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "অ্যাপের সাধারণ পছন্দ ও ব্যাকআপ ব্যবস্থাপনা",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Preferences Group
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column {
                    SettingsTile(
                        icon = Icons.Default.AttachMoney,
                        title = "মুদ্রা (Currency)",
                        subtitle = when (currentCurrency) {
                            "৳" -> "বাংলাদেশি টাকা (৳)"
                            "$" -> "US Dollar ($)"
                            "€" -> "Euro (€)"
                            "₹" -> "Indian Rupee (₹)"
                            else -> currentCurrency
                        },
                        onClick = { isCurrencyDialogOpen = true },
                        tag = "setting_currency"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SettingsTile(
                        icon = Icons.Default.DarkMode,
                        title = "থিম (Theme)",
                        subtitle = when (currentTheme) {
                            "LIGHT" -> "লাইট (Light)"
                            "DARK" -> "ডার্ক (Dark)"
                            else -> "সিস্টেম ডিফল্ট (System Default)"
                        },
                        onClick = { isThemeDialogOpen = true },
                        tag = "setting_theme"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SettingsTile(
                        icon = Icons.Default.Language,
                        title = "ভাষা (Language)",
                        subtitle = if (currentLanguage == "EN") "English" else "বাংলা",
                        onClick = { isLanguageDialogOpen = true },
                        tag = "setting_language"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SettingsTile(
                        icon = Icons.Default.Paid,
                        title = "মাসিক বাজেট",
                        subtitle = CurrencyUtils.formatAmount(monthlyBudget, currentCurrency),
                        onClick = onUpdateBudget,
                        tag = "setting_budget"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SettingsTile(
                        icon = Icons.Default.Category,
                        title = "ক্যাটাগরি ব্যবস্থাপনা",
                        subtitle = "কাস্টম খরচের ও আয়ের খাত যোগ বা পরিবর্তন",
                        onClick = onManageCategories,
                        tag = "setting_categories"
                    )
                }
            }
        }

        // Backup & Export Group (MANDATED by Prompt)
        item {
            Text(
                text = "ব্যাকআপ ও এক্সপোর্ট",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column {
                    SettingsTile(
                        icon = Icons.Default.Backup,
                        title = "CSV ফাইল হিসেবে এক্সপোর্ট করুন",
                        subtitle = "এক্সেল বা স্প্রেডশিটে ব্যবহারের উপযোগী ফাইল",
                        onClick = onExportCsv,
                        tag = "export_csv_tile"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SettingsTile(
                        icon = Icons.Default.UploadFile,
                        title = "JSON ব্যাকআপ ফাইল তৈরি করুন",
                        subtitle = "সম্পূর্ণ অ্যাপ ডাটার নিরাপদ ব্যাকআপ",
                        onClick = onExportJson,
                        tag = "export_json_tile"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SettingsTile(
                        icon = Icons.Default.Download,
                        title = "ডাটা ইমপোর্ট করুন (ফাইল নির্বাচন)",
                        subtitle = "পূর্বে ব্যাকআপ করা CSV বা JSON ফাইল লোড করুন",
                        onClick = {
                            filePickerLauncher.launch(arrayOf("text/*", "application/json", "*/*"))
                        },
                        tag = "import_file_tile"
                    )
                }
            }
        }

        // Danger Zone: Clear All Data
        item {
            Text(
                text = "ঝুঁকিপূর্ণ কার্যক্রম",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ExpenseRed
                ),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                SettingsTile(
                    icon = Icons.Default.DeleteForever,
                    title = "সকল ডাটা মুছে ফেলুন",
                    subtitle = "সমস্ত হিসাব ও সেটিংস প্রাথমিক অবস্থায় ফিরিয়ে নিন",
                    onClick = onClearAllData,
                    iconTint = ExpenseRed,
                    tag = "clear_all_data_tile"
                )
            }
        }

        // About App Group
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                SettingsTile(
                    icon = Icons.Default.Info,
                    title = "অ্যাপ সম্পর্কে (About App)",
                    subtitle = "আমার দৈনিক খরচ • সংস্করণ ১.০ (অফলাইন মোড)",
                    onClick = { isAboutDialogOpen = true },
                    tag = "about_app_tile"
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // Currency Dialog
    if (isCurrencyDialogOpen) {
        val currencies = listOf(
            "৳" to "বাংলাদেশি টাকা (৳)",
            "$" to "US Dollar ($)",
            "€" to "Euro (€)",
            "₹" to "Indian Rupee (₹)"
        )
        AlertDialog(
            onDismissRequest = { isCurrencyDialogOpen = false },
            title = { Text("মুদ্রা নির্বাচন করুন", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    currencies.forEach { (sym, label) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (currentCurrency == sym) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCurrencyChange(sym)
                                    isCurrencyDialogOpen = false
                                }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (currentCurrency == sym) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { isCurrencyDialogOpen = false }) { Text("বন্ধ") }
            }
        )
    }

    // Theme Dialog
    if (isThemeDialogOpen) {
        val themes = listOf(
            "SYSTEM" to "সিস্টেম ডিফল্ট (System Default)",
            "LIGHT" to "লাইট থিম (Light)",
            "DARK" to "ডার্ক থিম (Dark)"
        )
        AlertDialog(
            onDismissRequest = { isThemeDialogOpen = false },
            title = { Text("থিম নির্বাচন করুন", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    themes.forEach { (mode, label) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (currentTheme == mode) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onThemeChange(mode)
                                    isThemeDialogOpen = false
                                }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (currentTheme == mode) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { isThemeDialogOpen = false }) { Text("বন্ধ") }
            }
        )
    }

    // Language Dialog
    if (isLanguageDialogOpen) {
        val languages = listOf(
            "BN" to "বাংলা (Bangla - ডিফল্ট)",
            "EN" to "English"
        )
        AlertDialog(
            onDismissRequest = { isLanguageDialogOpen = false },
            title = { Text("ভাষা নির্বাচন করুন", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    languages.forEach { (code, label) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (currentLanguage == code) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLanguageChange(code)
                                    isLanguageDialogOpen = false
                                }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (currentLanguage == code) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { isLanguageDialogOpen = false }) { Text("বন্ধ") }
            }
        )
    }

    // About Dialog
    if (isAboutDialogOpen) {
        AlertDialog(
            onDismissRequest = { isAboutDialogOpen = false },
            title = {
                Text(
                    text = "আমার দৈনিক খরচ (My Daily Expense)",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "ভার্সন: ১.০.০", fontWeight = FontWeight.SemiBold)
                    Text(text = "এটি একটি সম্পূর্ণ অফলাইন ব্যক্তিগত আয় ও ব্যয়ের হিসাব ট্র্যাকিং অ্যাপ। কোন ইন্টারনেটের প্রয়োজন নেই এবং আপনার কোন তথ্য সার্ভারে পাঠানো হয় না। সকল ডাটা আপনার ডিভাইসের সুরক্ষিত লোকাল ডাটাবেজে সংরক্ষিত থাকে।")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "বৈশিষ্ট্যসমূহ:", fontWeight = FontWeight.Bold)
                    Text(text = "• আজকের ও মাসিক খরচের সার্বিক হিসাব\n• খাতভিত্তিক পাই চার্ট ও দৈনিক বার চার্ট\n• ৮০% ও ১০০% বাজেট অতিক্রমের পূর্বাভাস ও সতর্কতা\n• সম্পূর্ণ অফলাইন লোকাল রুম ডাটাবেজ\n• CSV ও JSON এক্সপোর্ট ও ব্যাকআপ সুবিধা")
                }
            },
            confirmButton = {
                Button(onClick = { isAboutDialogOpen = false }) {
                    Text("ঠিক আছে")
                }
            }
        )
    }
}

@Composable
private fun SettingsTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconTint: Color = EmeraldPrimary,
    tag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(14.dp)
        )
    }
}
