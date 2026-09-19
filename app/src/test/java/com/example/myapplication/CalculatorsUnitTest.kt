package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for FitTrack fitness calculators logic (BMI, TDEE, 1RM).
 */
class CalculatorsUnitTest {

    @Test
    fun testBmiCalculation() {
        val heightCm = 180.0
        val weightKg = 75.0
        val bmi = weightKg / ((heightCm / 100) * (heightCm / 100))

        assertEquals(23.148, bmi, 0.01)
        assertTrue(bmi >= 18.5 && bmi < 25.0) // Normal weight category
    }

    @Test
    fun testTdeeCalculationMale() {
        val age = 25
        val heightCm = 175.0
        val weightKg = 70.0
        // Mifflin-St Jeor Formula for Male: (10 * w) + (6.25 * h) - (5 * age) + 5
        val bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5
        val tdee = bmr * 1.55 // Moderately active multiplier

        assertEquals(1673.75, bmr, 0.01)
        assertEquals(2594.3125, tdee, 0.01)
    }

    @Test
    fun testOneRepMaxCalculation() {
        val weightKg = 100.0
        val reps = 10
        // Epley Formula: 1RM = weight * (1 + reps / 30.0)
        val oneRm = weightKg * (1 + (reps / 30.0))

        assertEquals(133.33, oneRm, 0.01)
    }
}
