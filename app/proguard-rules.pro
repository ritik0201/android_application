# FitTrack Proguard / R8 Keep Rules

# Room Database Keep Rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Kotlinx Serialization Keep Rules
-keepattributes *Annotation*,ElementValueAttribute
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# FitTrack Entities & Models
-keep class com.example.myapplication.data.entity.** { *; }
-keep class com.example.myapplication.data.dto.** { *; }
