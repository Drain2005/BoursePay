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

    testImplementation("junit:junit:4.13.2")
}

application {
    // Classe principale trouvée dans src/main/java/org/example/Main.java
    mainClass.set("org.example.Main")
}

tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.named<Jar>("jar") {
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }

    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })

    duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.EXCLUDE
}

tasks.register("cleanCompile") {
    dependsOn(tasks.named("clean"))
    dependsOn(tasks.named("compileJava"))
}