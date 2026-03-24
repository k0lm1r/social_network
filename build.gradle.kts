plugins {
    java
    id("com.google.protobuf") version "0.9.4" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("org.springframework.boot") version "4.0.4" apply false
    id("io.freefair.lombok") version "9.2.0" apply false
}

val mapstructVersion = "1.6.3"

allprojects {
    group = "com.kolmir"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }


    if (name != "proto") {
        apply(plugin = "io.spring.dependency-management")
        apply(plugin = "io.freefair.lombok")

        configurations {
            compileOnly {
                extendsFrom(configurations.annotationProcessor.get())
            }
        }

        dependencies {
            implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
            implementation("org.springframework.boot:spring-boot-starter-webmvc")
            implementation("org.springframework.boot:spring-boot-starter-validation")
            implementation("org.mapstruct:mapstruct:$mapstructVersion")

            annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
            annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
}
