# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# === R8 Full Mode: maximum shrinking + optimization ===
# Aggressive optimization passes (R8 honors this for full mode).
-optimizationpasses 5
-overloadaggressively
-repackageclasses ''
-allowaccessmodification
-dontwarn **
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify

# Strip all VERBOSE/DEBUG/INFO logs from release builds (WARN/ERROR kept).
# This is the R8-native replacement for -assumenosideeffects on android.util.Log.
-maximumremovedandroidloglevel INFO
-assumenosideeffects class java.io.PrintStream {
    public void println(...);
    public void print(...);
}
-assumenosideeffects class java.lang.System {
    public static void gc();
}
-assumenosideeffects class kotlin.io.ConsoleKt { *; }

# Room database rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep Room entities and DAOs
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class com.example.data.entities.** { *; }

# Keep annotations/signature for Room, Compose, coroutines, Biometric reflection.
-keepattributes Signature,InnerClasses,EnclosingMethod,*Annotation*
# Keep crash stack traces useful: line numbers are retained so the mapping.txt
# produced by R8 can de-obfuscate reports from the wild.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Biometric / crypto (kept only by name where reflection is used)
-keep class androidx.biometric.** { *; }
-dontwarn androidx.biometric.**

# Coroutines / Compose - keep ServiceLoader + serializer essentials, allow everything else to shrink
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class kotlin.Metadata { *; }

