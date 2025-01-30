# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Preserve Retrofit interfaces
-keepattributes Signature
-keep class * implements retrofit2.Call { *; }
-keep class * implements retrofit2.Response { *; }
-keep class * implements retrofit2.Converter { *; }

# Keep Retrofit annotations
-keepattributes RuntimeVisibleAnnotations

# Keep Retrofit API interfaces
-keep interface com.prafullkumar.productx.data.remote.** { *; }

# Keep Retrofit response models (if using Gson)
-keep class com.prafullkumar.productx.data.remote.dto.** { *; }

# Keep all @Serializable classes
-keep,allowobfuscation @kotlinx.serialization.Serializable class * { *; }

# Keep generated serializers
-keep class kotlinx.serialization.** { *; }

# Keep generated companion objects for serialization
-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class com.google.gson.** { *; }
-keep class com.prafullkumar.productx.data.remote.** { *; }