// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ktfmt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}

tasks.register<Copy>("installGitHooks") {
    from(file("${rootProject.rootDir}/gradle/hooks"))
    into(file("${rootProject.rootDir}/.git/hooks"))
    fileMode = 0x1ED // equivalent to 755 (rwxr-xr-x)
}

afterEvaluate {
    tasks.named("prepareKotlinBuildScriptModel") {
        dependsOn("installGitHooks")
    }
}

subprojects {
    apply(plugin = "com.ncorti.ktfmt.gradle")

    configure<com.ncorti.ktfmt.gradle.KtfmtExtension> {
        kotlinLangStyle()
    }
}
