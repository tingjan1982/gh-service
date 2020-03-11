/**
 * Describes the configurations need to incoroporate Spring Boot and Spring Data in Kotlin:
 *
 * https://spring.io/guides/tutorials/spring-boot-kotlin/
 */

plugins {
    // kotlin core
    kotlin("jvm") version "1.3.70"
    kotlin("kapt") version "1.3.70"

    // spring support
    id("org.springframework.boot") version "2.2.5.RELEASE"
    kotlin("plugin.spring") version "1.3.70"
    kotlin("plugin.jpa") version "1.3.70"

    // build info
    id("com.gorylenko.gradle-git-properties") version "2.2.2"

}

apply(plugin = "io.spring.dependency-management")

/**
 * https://stackoverflow.com/questions/41113268/how-to-set-up-kotlins-byte-code-version-in-gradle-project-to-java-8
 */
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
    kotlinOptions.jvmTarget = "1.8"
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources", "src/main/kotlin")
            exclude("*.kt")
        }
    }
}

repositories {
    mavenCentral()
    maven(url = "https://repo.spring.io/milestone")
}

dependencies {
    // kotlin 
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // security and oauth2
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-acl")
    implementation("org.springframework.security.oauth:spring-security-oauth2:2.2.5.RELEASE")
    //implementation("org.springframework.security:spring-security-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-jwt:1.1.0.RELEASE")

    // swagger 2
    implementation("io.springfox:springfox-swagger2:2.7.0")
    implementation("io.springfox:springfox-swagger-ui:2.7.0")

    runtimeOnly("org.hsqldb:hsqldb")
    implementation("net.sf.ehcache:ehcache:2.10.5")


    // test dependency
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("com.willowtreeapps.assertk:assertk:0.10")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

springBoot {
    buildInfo {
        properties {
            group = "io.geekhub"
            artifact = "gh-service"
            name = "gh-service"
            version = "1.0.0"
        }
    }
}

/**
 * Copies build related info to IntelliJ output directory so /info works correctly.
 * This is not required when running gradle outside of IntelliJ.
 */
tasks {
    register("copyBuildInfo", Copy::class) {
        dependsOn("bootBuildInfo")

        from("${buildDir}/resources/main/git.properties", "${buildDir}/resources/main/META-INF")
        into("out/production/resources/")
    }
}
