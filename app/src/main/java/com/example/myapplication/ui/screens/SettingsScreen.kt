package com.example.myapplication.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current

    val unit by viewModel.weightUnit.collectAsState()
    val theme by viewModel.appTheme.collectAsState()
    val workoutReminders by viewModel.workoutReminderEnabled.collectAsState()
    val waterReminders by viewModel.waterReminderEnabled.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showCalculatorsDialog by remember { mutableStateOf(false) }
    var backupStatusMessage by remember { mutableStateOf<String?>(null) }

    // Export JSON Backup file picker
    val exportBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            val json = viewModel.generateFullJsonBackup()
            context.contentResolver.openOutputStream(it)?.use { out ->
                out.write(json.toByteArray())
            }
            backupStatusMessage = "Backup exported successfully!"
        }
    }

    // Import JSON Backup file picker
    val importBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            val json = context.contentResolver.openInputStream(it)?.bufferedReader()?.use { reader ->
                reader.readText()
            }
            if (json != null) {
                viewModel.restoreFullJsonBackup(json) { success ->
                    backupStatusMessage = if (success) "Backup restored successfully!" else "Failed to restore backup file."
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Preferences & Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        // Calculators Shortcut Button
        OutlinedButton(
            onClick = { showCalculatorsDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Calculate, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Fitness Calculators (BMI, TDEE, 1RM)")
        }

        // Weight Unit
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Weight Units", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = unit == "kg", onClick = { viewModel.setWeightUnit("kg") }, label = { Text("Kilograms (kg)") })
                    FilterChip(selected = unit == "lb", onClick = { viewModel.setWeightUnit("lb") }, label = { Text("Pounds (lb)") })
                }
            }
        }

        // App Theme
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = theme == "system", onClick = { viewModel.setAppTheme("system") }, label = { Text("System") })
                    FilterChip(selected = theme == "dark", onClick = { viewModel.setAppTheme("dark") }, label = { Text("Dark") })
                    FilterChip(selected = theme == "light", onClick = { viewModel.setAppTheme("light") }, label = { Text("Light") })
                }
            }
        }

        // Reminders
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Reminders & Notifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily Workout Reminder")
                    Switch(checked = workoutReminders, onCheckedChange = { viewModel.toggleWorkoutReminder(it) })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Periodic Water Reminder")
                    Switch(checked = waterReminders, onCheckedChange = { viewModel.toggleWaterReminder(it) })
                }
            }
        }

        // Data Management, Backup & CSV
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Backup & Data Export", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { exportBackupLauncher.launch("FitTrack_Backup_${System.currentTimeMillis()}.json") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export JSON")
                    }

                    OutlinedButton(
                        onClick = { importBackupLauncher.launch(arrayOf("application/json")) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Import JSON")
                    }
                }

                OutlinedButton(
                    onClick = {
                        val csvData = viewModel.generateWorkoutCsv()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, csvData)
                            type = "text/csv"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Export Workout CSV")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Workout CSV")
                }

                Button(
                    onClick = { showClearDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear All App Data")
                }

                backupStatusMessage?.let { msg ->
                    Text(msg, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Privacy Policy
        OutlinedButton(
            onClick = { showPrivacyDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.PrivacyTip, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Privacy Policy")
        }

        if (showCalculatorsDialog) {
            AlertDialog(
                onDismissRequest = { showCalculatorsDialog = false },
                title = { Text("Fitness Calculators") },
                text = { CalculatorsScreen() },
                confirmButton = { TextButton(onClick = { showCalculatorsDialog = false }) { Text("Close") } }
            )
        }

        if (showPrivacyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                title = { Text("FitTrack Privacy Policy 🔒") },
                text = {
                    Text(
                        "FitTrack is 100% offline-first. Your personal data, workouts, body measurements, meal logs, and progress photos stay strictly on your device.\n\n" +
                        "• No external servers or cloud uploads.\n" +
                        "• No user tracking or analytics.\n" +
                        "• You have complete ownership of your data with Full JSON Backup & Export."
                    )
                },
                confirmButton = { TextButton(onClick = { showPrivacyDialog = false }) { Text("Got it") } }
            )
        }

        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("Clear All Data?") },
                text = { Text("Are you sure you want to clear all profile, workout, and nutrition data? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearAllData()
                            showClearDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Clear All")
                    }
                },
                dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text("Cancel") } }
            )
        }
    }
}
