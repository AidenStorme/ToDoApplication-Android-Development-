// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Hilt en KSP moeten in dezelfde scope gedeclareerd worden (zie dagger issue 3965)
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
