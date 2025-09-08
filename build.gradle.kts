plugins {
    java
    id("org.springframework.boot") version "3.5.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.google.cloud.tools.jib") version "3.4.0"
}

allprojects {
    group = "click.dailyfeed"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "com.google.cloud.tools.jib")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

project(":dailyfeed-content") {
    jib {
        // Base 이미지 설정 (Java 17 기반)
        from {
            // Google의 distroless 이미지 사용 (인증 불필요)
            image = "gcr.io/distroless/java17-debian12"
            // 보안 강화된 최소한의 베이스 이미지
//            image = "amazoncorretto:17"
            // image = "eclipse-temurin:17-jre-alpine"
            // 또는 더 작은 이미지를 원한다면: "gcr.io/distroless/java17-debian11"
        }

        // 타겟 이미지 설정
        to {
            tags = setOf("0.0.1")
            image = "alpha300uk/dailyfeed-content-svc"
        }

        // 컨테이너 설정
        container {
            // JVM 옵션
            jvmFlags = listOf(
                // Spring profile은 Helm에서 환경변수로 주입하므로 제거
                "-XX:+UseContainerSupport",
                "-XX:+UseG1GC",
//                "-verbose:gc",
//                "-XX:+PrintGCDetails",
//                "-Dserver.port=8080",
                "-Dfile.encoding=UTF-8",
            )

            // 레이블
            labels = mapOf(
                "maintainer" to "alpha300uk@gmail.com",
                "version" to project.version.toString(),
                "description" to project.description.toString()
            )

            // 작업 디렉토리
            workingDirectory = "/app"

            // 사용자 설정 (보안을 위해 non-root 사용자 사용)
            user = "1000:1000"

            // 생성 시간 설정 (재현 가능한 빌드를 위해)
            creationTime = "USE_CURRENT_TIMESTAMP"
        }
    }
}