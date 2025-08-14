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
    // MySQL Driver
    implementation ("mysql:mysql-connector-java:8.0.33")

    // HikariCP pour la gestion des connexions
    implementation ("com.zaxxer:HikariCP:5.0.1")

    // Logging
    implementation ("org.slf4j:slf4j-api:2.0.7")
    implementation ("ch.qos.logback:logback-classic:1.4.8")

    // Email
    implementation ("javax.mail:mail:1.5.0-b01")
    implementation ("com.sun.mail:javax.mail:1.6.2")

    // PDF Generation
    implementation ("com.itextpdf:itextpdf:5.5.13.3")

    // Testing
    testImplementation ("junit:junit:4.13.2")
}

application {
    mainClass = "org.example.Main"
}

compileJava {
    options.encoding = "UTF-8"
}

jar {
    manifest {
        attributes "Main-Class": ("org.example.Main")
    }
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Tâche pour nettoyer et recompiler
task cleanCompile {
    dependsOn 'clean', 'compileJava'
}

// Configuration pour l'encoding UTF-8
tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}
Améliorer
Expliquer
