plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.telegram:telegrambots-longpolling:8.3.0")
    implementation("org.telegram:telegrambots-client:8.3.0")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("com.openai:openai-java:1.6.1")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")

    implementation(files("libs/gemini-api-1beta.0.2.8-SNAPSHOT.jar"))
    implementation(files("libs/gemini-gson-1beta.0.2.8-SNAPSHOT.jar"))



//    implementation("swiss.ameri:gemini-api:1beta.0.2.8-SNAPSHOT")
//    implementation("swiss.ameri:gemini-gson:1beta.0.2.8-SNAPSHOT")

//    implementation("com.github.HatKid21:gemini-api:main")
//    implementation("com.github.HatKid21:gemini-gson:main")

//    implementation("com.github.HatKid21:gemini-api:master")



//    implementation("swiss.ameri:gemini-api:1beta.0.2.7")
//    implementation("swiss.ameri:gemini-gson:1beta.0.2.7")

    implementation("org.commonmark:commonmark:0.21.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks{
    shadowJar{
        manifest {
            attributes["Main-class"] ="com.github.hatkid.Server"
        }
    }
}