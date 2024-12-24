plugins {
    id("java")
}

group = "ru.rudikov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.17.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.0")
    testImplementation("org.awaitility:awaitility:4.2.2")
    testImplementation("org.instancio:instancio-junit:5.2.1")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testImplementation("io.rest-assured:rest-assured:5.5.0")
}

tasks.test {
    useJUnitPlatform()
}