pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// Centralized repository management for the entire project.
// This is the single source of truth for where Gradle should download dependencies.
dependencyResolutionManagement {
    // Enforce that repositories are only declared here and not in sub-project build.gradle files.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Google's Maven repository. Required for Android, Google Play Services, and other Google libraries.
        google()
        // Maven Central repository. A major repository for many open-source libraries.
        mavenCentral()
    }
}

rootProject.name = "Ví QR"
include(":app")
