plugins {
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.5"
    id("java")
    id("jacoco")
}

group = "com.kestrel.veritafi"
version = "1.0.0"
description = "VeritaFi background check project for CCIS"

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

repositories {
    mavenCentral()
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources", "src/main/java")
        }
    }
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-devtools")
    
    // MyBatis (iBatis)
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
    
    // Jackson
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema:2.15.3")
    
    // Caching
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // OpenAPI/Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    
    // JSON Schema validation
    implementation("com.networknt:json-schema-validator:1.0.87")
    
    // Reflection
    implementation("org.reflections:reflections:0.10.2")
    
    // Development tools
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:3.0.3")
    testImplementation("org.projectlombok:lombok")
    testImplementation("com.h2database:h2:2.2.224")
    
    // SQL Server JDBC driver
    runtimeOnly("com.microsoft.sqlserver:mssql-jdbc:12.6.1.jre17")
    
    // Annotation Processors
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    
    testAnnotationProcessor("org.projectlombok:lombok")

    // Bring in any shared jars from vendor-auth-service here (drop them into libs/)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Create bootJar with a consistent name for Docker build
val bootJar by tasks.getting(org.springframework.boot.gradle.tasks.bundling.BootJar::class) {
    archiveFileName.set("app.jar")
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.90".toBigDecimal()
            }
        }
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
