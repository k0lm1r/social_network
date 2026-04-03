plugins {
    java
    id("com.google.protobuf") version "0.9.4" apply false
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springframework.boot") version "4.0.5" apply false
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

extra["springCloudVersion"] = "2025.1.1"


subprojects {
    apply(plugin = "java")
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    if (name != "proto") {
        apply(plugin = "org.springframework.boot")
        apply(plugin = "io.spring.dependency-management")
        apply(plugin = "io.freefair.lombok")

        dependencyManagement {
            imports {
                mavenBom(
                    "org.springframework.cloud:spring-cloud-dependencies:${rootProject.extra["springCloudVersion"]}"
                )
            }
        }

        configurations {
            compileOnly {
                extendsFrom(configurations.annotationProcessor.get())
            }
        }

        dependencies {
            implementation("org.springframework.boot:spring-boot-starter-web")
            implementation("org.springframework.boot:spring-boot-starter-validation")
            implementation("org.springframework.boot:spring-boot-starter-actuator")

            implementation("org.mapstruct:mapstruct:$mapstructVersion")
            annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
            annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
            testImplementation("org.springframework.boot:spring-boot-starter-test")
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
}
