pluginManagement {
    repositories {
        google()
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
rootProject.name = "Brelio"

include(":app")
include(":domain")
include(":data")
include(":core:common")
include(":core:network")
include(":core:designsystem")
include(":feature:auth")
include(":feature:onboarding")
include(":feature:home")
include(":feature:calendar")
include(":feature:clients")
include(":feature:settings")
