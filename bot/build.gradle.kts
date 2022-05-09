import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
}

group = "uz.warcom.contest"
version = "0.0.3"
java.sourceCompatibility = JavaVersion.VERSION_11
val mapstructVersion = "1.5.0.RC1"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.mapstruct:mapstruct-processor:${mapstructVersion}")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation ("org.telegram:telegrambots-abilities:5.7.1")
    implementation(project(":persistence"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.mapstruct:mapstruct:${mapstructVersion}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("prepareKotlinBuildScriptModel") {}

tasks.register("wrapper") {}

tasks.withType<Wrapper> {
    gradleVersion = "7.4.1"
}