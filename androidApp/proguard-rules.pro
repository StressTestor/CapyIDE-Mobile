# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# kotlinx-serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class dev.capyide.mobile.**$$serializer { *; }
-keepclassmembers class dev.capyide.mobile.** { *** Companion; }
-keepclasseswithmembers class dev.capyide.mobile.** { kotlinx.serialization.KSerializer serializer(...); }

# Koin
-keep class org.koin.** { *; }
-dontwarn org.koin.**
