plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-classic:1.4.8")
    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("com.itextpdf:itextpdf:5.5.13.3")

    // AJOUTEZ CES 2 LIGNES POUR FLATLAF
    implementation("com.formdev:flatlaf:3.4.1")
    implementation("com.formdev:flatlaf-extras:3.4.1")

    testImplementation("junit:junit:4.13.2")
}

application {
    mainClass.set("org.example.Main")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}