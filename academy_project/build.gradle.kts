import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.1" // Defines version of Spring Boot
	id("io.spring.dependency-management") version "1.0.11.RELEASE" // Handles Spring Boot dependencies
	kotlin("jvm") version "1.7.0"
	kotlin("plugin.spring") version "1.7.0"
}


group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web") // Adds web functionality
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test") // Adds Spring Boot test
	testImplementation("com.ninja-squad:springmockk:3.1.1") // Used for using Mockk with Spring
}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
