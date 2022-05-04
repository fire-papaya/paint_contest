import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa") version "1.6.20"
    id("org.flywaydb.flyway") version "8.5.7"
}

group = "uz.warcom.contest"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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

// only for local db
flyway {
    url = "jdbc:mariadb://localhost:3310/paint_contest"
    user = "root"
    password = ""
    locations = arrayOf("classpath:db/migration")
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath ("org.flywaydb:flyway-mysql:8.5.7")
    }
}

tasks.register("prepareKotlinBuildScriptModel") {}

tasks.register("wrapper") {}

tasks.withType<Wrapper> {
    gradleVersion = "7.4.1"
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
