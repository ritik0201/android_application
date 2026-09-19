package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class CalculatorsViewModel : ViewModel() {

    private val _bmiResult = MutableStateFlow<String?>(null)
    val bmiResult: StateFlow<String?> = _bmiResult.asStateFlow()

    private val _tdeeResult = MutableStateFlow<String?>(null)
    val tdeeResult: StateFlow<String?> = _tdeeResult.asStateFlow()

    private val _oneRmResult = MutableStateFlow<String?>(null)
    val oneRmResult: StateFlow<String?> = _oneRmResult.asStateFlow()

    fun calculateBmi(heightCmStr: String, weightKgStr: String) {
        val h = heightCmStr.toDoubleOrNull() ?: 0.0
        val w = weightKgStr.toDoubleOrNull() ?: 0.0
        if (h <= 0 || w <= 0) {
            _bmiResult.value = "Please enter valid height and weight."
            return
        }
        val bmi = w / ((h / 100) * (h / 100))
        val category = when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal Weight"
            bmi < 30.0 -> "Overweight"
            else -> "Obese"
        }
        _bmiResult.value = String.format(Locale.getDefault(), "BMI: %.1f (%s)", bmi, category)
    }

    fun calculateTdee(ageStr: String, gender: String, heightCmStr: String, weightKgStr: String, activityMultiplier: Double) {
        val age = ageStr.toIntOrNull() ?: 0
        val h = heightCmStr.toDoubleOrNull() ?: 0.0
        val w = weightKgStr.toDoubleOrNull() ?: 0.0
        if (age <= 0 || h <= 0 || w <= 0) {
            _tdeeResult.value = "Please enter valid age, height, and weight."
            return
        }

        val bmr = if (gender.equals("Male", ignoreCase = true)) {
            (10 * w) + (6.25 * h) - (5 * age) + 5
        } else {
            (10 * w) + (6.25 * h) - (5 * age) - 161
        }

        val tdee = bmr * activityMultiplier
        val weightLossCals = tdee - 500
        val weightGainCals = tdee + 500

        _tdeeResult.value = String.format(
            Locale.getDefault(),
            "BMR: %.0f kcal\nMaintenance (TDEE): %.0f kcal/day\nWeight Loss (-0.5kg/wk): %.0f kcal\nWeight Gain (+0.5kg/wk): %.0f kcal",
            bmr, tdee, weightLossCals, weightGainCals
        )
    }

    fun calculateOneRepMax(weightKgStr: String, repsStr: String) {
        val w = weightKgStr.toDoubleOrNull() ?: 0.0
        val r = repsStr.toIntOrNull() ?: 0
        if (w <= 0 || r <= 0) {
            _oneRmResult.value = "Please enter valid weight and reps."
            return
        }

        val oneRm = w * (1 + (r / 30.0))
        _oneRmResult.value = String.format(
            Locale.getDefault(),
            "Estimated 1RM: %.1f kg\n95%% 1RM: %.1f kg (2 reps)\n90%% 1RM: %.1f kg (4 reps)\n85%% 1RM: %.1f kg (6 reps)\n80%% 1RM: %.1f kg (8 reps)",
            oneRm, oneRm * 0.95, oneRm * 0.90, oneRm * 0.85, oneRm * 0.80
        )
    }
}
