//enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
// buildSrc support
//enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement.repositories {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    mavenCentral()
    google()
}

rootProject.name = "AccSettings"
include(":app")
