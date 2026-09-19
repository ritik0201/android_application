package com.example.myapplication.data.database

import com.example.myapplication.data.entity.Exercise

/**
 * Preloaded exercise library data seeded on app first launch (40+ exercises).
 */
object ExerciseSeedData {
    val initialExercises = listOf(
        // Chest
        Exercise(name = "Bench Press", muscleGroup = "Chest", equipment = "Barbell", instructions = "Lie flat on the bench, lower the barbell controlled to lower chest, press explosively upward."),
        Exercise(name = "Incline Dumbbell Press", muscleGroup = "Chest", equipment = "Dumbbell", instructions = "Lie on an incline bench at 30-45 degrees, press dumbbells overhead until arms are extended."),
        Exercise(name = "Push-Ups", muscleGroup = "Chest", equipment = "Bodyweight", instructions = "Keep core tight and back straight. Lower chest toward floor, push back up."),
        Exercise(name = "Cable Flyes", muscleGroup = "Chest", equipment = "Cable", instructions = "Set handles at chest height, bring handles together in front of chest in a hugging motion."),
        Exercise(name = "Chest Dips", muscleGroup = "Chest", equipment = "Bodyweight", instructions = "Lean torso slightly forward on dip bars, lower body until elbows reach 90 degrees, push up."),

        // Back
        Exercise(name = "Pull-Ups", muscleGroup = "Back", equipment = "Bodyweight", instructions = "Grasp bar with palms facing away, pull chest up to the bar, lower with control."),
        Exercise(name = "Chin-Ups", muscleGroup = "Back", equipment = "Bodyweight", instructions = "Grasp bar with palms facing you, pull chest to bar engaging biceps and lats."),
        Exercise(name = "Barbell Row", muscleGroup = "Back", equipment = "Barbell", instructions = "Bend at hips with flat back, pull bar to lower abdomen, lower with control."),
        Exercise(name = "Lat Pulldown", muscleGroup = "Back", equipment = "Cable", instructions = "Sit at machine, pull bar down to upper chest while squeezing shoulder blades together."),
        Exercise(name = "Single-Arm Dumbbell Row", muscleGroup = "Back", equipment = "Dumbbell", instructions = "Rest knee and hand on bench, pull dumbbell to hip keeping elbow close to body."),
        Exercise(name = "Deadlift", muscleGroup = "Back", equipment = "Barbell", instructions = "Hinge at hips, keep back flat, stand tall by driving hips forward and pulling barbell up legs."),

        // Legs
        Exercise(name = "Barbell Squat", muscleGroup = "Legs", equipment = "Barbell", instructions = "Rest bar on upper back, squat down until thighs are parallel to ground, push through heels to stand."),
        Exercise(name = "Leg Press", muscleGroup = "Legs", equipment = "Machine", instructions = "Place feet hip-width on platform, lower weight slowly until knees form 90 degrees, press up."),
        Exercise(name = "Romanian Deadlift", muscleGroup = "Legs", equipment = "Barbell", instructions = "Slight knee bend, hinge at hips while sliding bar down shins until hamstrings stretch, return up."),
        Exercise(name = "Dumbbell Lunges", muscleGroup = "Legs", equipment = "Dumbbell", instructions = "Hold dumbbells at sides, step forward, lower back knee toward ground, push back to start."),
        Exercise(name = "Leg Extension", muscleGroup = "Legs", equipment = "Machine", instructions = "Sit on machine, extend knees to lift padded lever up, squeeze quadriceps at top."),
        Exercise(name = "Leg Curl", muscleGroup = "Legs", equipment = "Machine", instructions = "Lie face down or sit, curl heels toward glutes to work hamstrings, lower slowly."),
        Exercise(name = "Standing Calf Raises", muscleGroup = "Legs", equipment = "Bodyweight", instructions = "Stand on edge of step, raise up onto toes as high as possible, lower heels below step level."),

        // Shoulders
        Exercise(name = "Overhead Shoulder Press", muscleGroup = "Shoulders", equipment = "Barbell", instructions = "Hold bar at collarbone, press bar straight overhead overhead until arms lock out."),
        Exercise(name = "Dumbbell Lateral Raise", muscleGroup = "Shoulders", equipment = "Dumbbell", instructions = "Stand tall, raise dumbbells out to sides until parallel with floor with slight elbow bend."),
        Exercise(name = "Arnold Press", muscleGroup = "Shoulders", equipment = "Dumbbell", instructions = "Start with dumbbells at chest palms in, rotate palms outward as you press overhead."),
        Exercise(name = "Rear Delt Flyes", muscleGroup = "Shoulders", equipment = "Dumbbell", instructions = "Bend forward at waist, raise dumbbells out to sides squeezing rear shoulder muscles."),
        Exercise(name = "Dumbbell Front Raise", muscleGroup = "Shoulders", equipment = "Dumbbell", instructions = "Lift dumbbells in front of body to shoulder height with arms straight, lower slowly."),
        Exercise(name = "Face Pulls", muscleGroup = "Shoulders", equipment = "Cable", instructions = "Attach rope to cable pulley, pull toward forehead flaring elbows outward."),

        // Arms
        Exercise(name = "Barbell Bicep Curl", muscleGroup = "Arms", equipment = "Barbell", instructions = "Stand upright, keep elbows tucked to sides, curl barbell up toward chest."),
        Exercise(name = "Hammer Curls", muscleGroup = "Arms", equipment = "Dumbbell", instructions = "Hold dumbbells with neutral grip (palms facing each other), curl upward to shoulders."),
        Exercise(name = "Preacher Curl", muscleGroup = "Arms", equipment = "Barbell", instructions = "Rest upper arms on preacher pad, curl bar up toward face without lifting elbows off pad."),
        Exercise(name = "Concentration Curl", muscleGroup = "Arms", equipment = "Dumbbell", instructions = "Sit down, brace elbow against inner thigh, curl dumbbell up toward shoulder."),
        Exercise(name = "Tricep Rope Pushdown", muscleGroup = "Arms", equipment = "Cable", instructions = "Attach rope to high pulley, push down extending elbows, spread rope ends at bottom."),
        Exercise(name = "Skull Crushers", muscleGroup = "Arms", equipment = "Barbell", instructions = "Lie on bench, lower bar down toward forehead bending elbows, extend elbows to top."),
        Exercise(name = "Dumbbell Overhead Tricep Extension", muscleGroup = "Arms", equipment = "Dumbbell", instructions = "Hold dumbbell overhead with both hands, lower behind head by bending elbows, press up."),
        Exercise(name = "Tricep Dips", muscleGroup = "Arms", equipment = "Bodyweight", instructions = "Grasp bench or dip handles, lower body by bending elbows to 90 degrees, push up."),

        // Core
        Exercise(name = "Planks", muscleGroup = "Core", equipment = "Bodyweight", instructions = "Hold forearm plank position with straight body, engaged core, and level hips."),
        Exercise(name = "Crunches", muscleGroup = "Core", equipment = "Bodyweight", instructions = "Lie on back, bend knees, lift shoulders off floor contracting upper abdominals."),
        Exercise(name = "Russian Twists", muscleGroup = "Core", equipment = "Bodyweight", instructions = "Sit with torso leaned back at 45 degrees, rotate torso side to side touching hands to floor."),
        Exercise(name = "Hanging Leg Raise", muscleGroup = "Core", equipment = "Bodyweight", instructions = "Hang from pull-up bar, lift legs straight up until parallel to floor without swinging."),
        Exercise(name = "Ab Wheel Rollout", muscleGroup = "Core", equipment = "Equipment", instructions = "Kneel on floor, roll wheel forward extending body as far as possible, pull back with abdominals."),
        Exercise(name = "Mountain Climbers", muscleGroup = "Core", equipment = "Bodyweight", instructions = "Start in push-up position, rapidly alternate driving knees toward chest."),

        // Full Body
        Exercise(name = "Burpees", muscleGroup = "Full Body", equipment = "Bodyweight", instructions = "From standing, squat down, kick feet back to push-up, perform push-up, jump back up."),
        Exercise(name = "Kettlebell Swings", muscleGroup = "Full Body", equipment = "Kettlebell", instructions = "Hinge at hips, swing kettlebell between legs, drive hips forcefully to swing kettlebell to chest height."),
        Exercise(name = "Dumbbell Thrusters", muscleGroup = "Full Body", equipment = "Dumbbell", instructions = "Hold dumbbells at shoulders, perform full squat, press dumbbells overhead as you explode up."),
        Exercise(name = "Farmer's Walk", muscleGroup = "Full Body", equipment = "Dumbbell", instructions = "Pick up heavy dumbbells at sides, walk forward with upright posture and firm grip.")
    )
}
