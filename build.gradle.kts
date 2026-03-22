plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("org.springframework.boot") version "4.0.4" apply false
}

allprojects {
    group = "com.kolmir"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

val mapstructVersion = "1.6.3"

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.boot:spring-boot-starter-webmvc")
        implementation("org.mapstruct:mapstruct:$mapstructVersion")
        implementation("org.liquibase:liquibase-core")

        compileOnly("org.projectlombok:lombok")
        runtimeOnly("org.postgresql:postgresql")
        annotationProcessor("org.projectlombok:lombok")
        annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
        annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
        
        testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
        testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
