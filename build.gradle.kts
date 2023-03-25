import Versions.configureJavaToolchain
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
  kotlin("jvm") version Versions.KOTLIN
  id("io.gitlab.arturbosch.detekt") version Versions.DETEKT
  id("com.jaredsburrows.license") version "0.9.0"
  id("com.autonomousapps.dependency-analysis") version "1.19.0"
  `maven-publish`
}

buildscript {
  repositories {
    mavenCentral()
  }
}

java {
  toolchain {
    configureJavaToolchain()
  }
}

kotlin {
  jvmToolchain {
    configureJavaToolchain()
  }
}

apply {
  plugin("com.jaredsburrows.license")
  plugin("org.jetbrains.kotlin.jvm")
  plugin("io.gitlab.arturbosch.detekt")
  plugin("maven-publish")
}

group = "com.github.nava2.kinterval-tree"
version = "0.1.0"

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.google.guava:guava:31.1-jre")

  testImplementation("org.assertj:assertj-core:3.24.2")

  val junitVersion = "5.9.1"
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["kotlin"])
      pom {
        licenses {
          license {
            name.set("MIT License")
            url.set("https://opensource.org/licenses/MIT")
          }
        }
        developers {
          developer {
            name.set("Mason M Lai")
          }
          developer {
            name.set("Kevin Brightwell")
            email.set("kevin.brightwell2+interval-tree@gmail.com")
          }
        }
        scm {
          url.set("https://github.com/Nava2/kinterval-tree")
        }
      }
    }
  }
}

licenseReport {
  generateTextReport = true
  generateHtmlReport = true
  generateCsvReport = false
  generateJsonReport = false
}

tasks.test {
  useJUnitPlatform()
}

detekt {
  parallel = true
  autoCorrect = true

  buildUponDefaultConfig = true // preconfigure defaults
  config = files("$rootDir/config/detekt-config.yml")

  allRules = false // activate all available (even unstable) rules.
}

dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.DETEKT}")
}

tasks.named("check") {
  dependsOn(tasks.named("projectHealth"))
}

tasks.withType<Detekt>().configureEach {
  jvmTarget = "18"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
  jvmTarget = "18"
}

tasks.withType<Detekt>().configureEach {
  reports {
    html.required.set(true)
  }
}
