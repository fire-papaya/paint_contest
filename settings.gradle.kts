plugins {
    kotlin("jvm") version "1.6.20" apply false
    kotlin("plugin.spring") version "1.6.20" apply false
    kotlin("kapt") version "1.6.20" apply false
}

rootProject.name = "paint_contest"

include ("bot")
include ("persistence")