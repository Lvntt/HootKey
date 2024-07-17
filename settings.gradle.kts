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
    }
}

rootProject.name = "HootKey"
include(":app")
include(":feature-auth")
include(":feature-auth:feature-auth-api")
include(":feature-auth:feature-auth-impl")
include(":core")
include(":core:core-common-api")
include(":core:core-common-impl")
include(":core:core-utils")
