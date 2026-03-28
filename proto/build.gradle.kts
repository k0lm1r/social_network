import com.google.protobuf.gradle.*

plugins {
    `java-library`
    id("com.google.protobuf")
}

description = "Proto file and its java implementation for communication between API Gateway and Identity Sevice by gRPC"

val grpcVersion = "1.80.0"
val protobufVersion = "3.25.8"

dependencies {
    api("com.google.protobuf:protobuf-java:$protobufVersion")
    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("io.grpc:grpc-stub:$grpcVersion")

    compileOnly("org.apache.tomcat:annotations-api:6.0.53")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }

    generateProtoTasks {
        all().configureEach {
            plugins {
                id("grpc") {}
            }
        }
    }
}