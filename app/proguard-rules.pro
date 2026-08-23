# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Room database rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep entities and DAOs
-keep class com.example.data.entities.** { *; }
-keep interface com.example.data.dao.** { *; }
-keep class com.example.data.db.** { *; }

