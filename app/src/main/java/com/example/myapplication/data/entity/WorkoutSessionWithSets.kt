package com.example.myapplication.data.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Relation class bundling a WorkoutSession with its logged sets.
 */
data class WorkoutSessionWithSets(
    @Embedded val session: WorkoutSession,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val sets: List<LoggedSet>
)
