/**
 * Describes the configurations need to incorporate Spring Boot and Spring Data in Kotlin:
 *
 * https://spring.io/guides/tutorials/spring-boot-kotlin/
 *
 * Passing arguments to bootRun task:
 * https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/
 */

plugins {
    // kotlin core
    kotlin("jvm") version "1.6.21"
    kotlin("kapt") version "1.6.21"

    // spring support
    id("org.springframework.boot") version "2.5.14"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.noarg") version "1.6.21"
//    kotlin("plugin.allopen") version "1.4.32" // plugin.spring offers the same functionality - https://kotlinlang.org/docs/all-open-plugin.html#spring-support

    // build info
    id("com.gorylenko.gradle-git-properties") version "2.4.1"

}

apply(plugin = "io.spring.dependency-management")

version = "1.0.0"

/**
 * https://stackoverflow.com/questions/41113268/how-to-set-up-kotlins-byte-code-version-in-gradle-project-to-java-8
 */
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
    kotlinOptions.jvmTarget = "17"
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
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // security and oauth2
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.security.oauth:spring-security-oauth2:2.5.2.RELEASE")

    implementation("org.apache.httpcomponents:httpclient")
    
    // swagger 2
    implementation("io.springfox:springfox-swagger2:3.0.0")
    implementation("io.springfox:springfox-swagger-ui:3.0.0")
    implementation("org.jsoup:jsoup:1.14.3")

    runtimeOnly("org.hsqldb:hsqldb")
    implementation("net.sf.ehcache:ehcache:2.10.9.2")


    // test dependency
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("com.willowtreeapps.assertk:assertk:0.25")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // embedded mongodb
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
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

    "processResources"(Copy::class) {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    "test"(Test::class) {
        useJUnitPlatform()
    }
}
