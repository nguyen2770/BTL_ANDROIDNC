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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    
    // Khai báo version catalog trực tiếp thay vì dùng from()
    versionCatalogs {
        create("libs") {
            // Các phiên bản
            version("agp", "8.2.2")
            version("kotlin", "1.9.22")
            version("google-services", "4.4.2")
            
            // Các plugin
            plugin("android-application", "com.android.application").versionRef("agp")
            plugin("kotlin-android", "org.jetbrains.kotlin.android").versionRef("kotlin")
            plugin("google-services", "com.google.gms.google-services").versionRef("google-services")
        }
    }
}
rootProject.name = "BTL_Android"
include(":app")
