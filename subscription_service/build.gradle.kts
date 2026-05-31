plugins {
	java
	jacoco
}

description = "Social interaction processing service: subscriptions, likes, dislikes"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-starter-web")
	implementation(project(":logging-common"))
	implementation(project(":auth-contract"))
	implementation(project(":auth-spring-security"))
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

	testImplementation("io.cucumber:cucumber-java:7.20.1")
	testImplementation("io.cucumber:cucumber-junit-platform-engine:7.20.1")
	testImplementation("org.junit.platform:junit-platform-suite")
	testImplementation("org.glassfish.jersey.core:jersey-common:3.1.8")
	testImplementation("org.testcontainers:junit-jupiter:1.21.3")
	testImplementation("org.testcontainers:mongodb:1.21.3")
}

jacoco {
	toolVersion = "0.8.13"
}

val businessLogicCoverage = listOf(
	"com/kolmir/subscription_service/service/impl/**"
)

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)

	reports {
		xml.required.set(true)
		html.required.set(true)
	}

	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				include(businessLogicCoverage)
			}
		})
	)
}

tasks.jacocoTestCoverageVerification {
	dependsOn(tasks.test)

	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				include(businessLogicCoverage)
			}
		})
	)

	violationRules {
		rule {
			element = "BUNDLE"

			limit {
				counter = "LINE"
				value = "COVEREDRATIO"
				minimum = "0.80".toBigDecimal()
			}
		}
	}
}

tasks.check {
	dependsOn(tasks.jacocoTestCoverageVerification)
}
