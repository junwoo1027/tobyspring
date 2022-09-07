plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.24")
    implementation("mysql:mysql-connector-java:8.0.30")
    implementation("org.springframework:spring-context:5.3.22")
    implementation("org.springframework:spring-jdbc:5.3.22")
    implementation("junit:junit:4.13.1")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.7")
    testImplementation("org.hamcrest:hamcrest:2.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
