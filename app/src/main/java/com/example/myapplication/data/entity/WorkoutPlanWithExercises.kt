package com.example.myapplication.data.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Relation class bundling a WorkoutPlan with its associated list of PlanExercises.
 */
data class WorkoutPlanWithExercises(
    @Embedded val plan: WorkoutPlan,
    @Relation(
        parentColumn = "id",
        entityColumn = "planId"
    )
    val exercises: List<PlanExercise>
)
