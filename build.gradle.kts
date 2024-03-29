plugins {
  java
  `maven-publish`
  kotlin("jvm") version "1.7.10"
  id("org.springframework.boot") version "2.7.2"
}

val projectVersion: String by project
group = "io.openlegacy"
version = projectVersion

repositories {
  val olUsername = "public"
  val olPassword = "AP3UUPpoUv5HMRpNWwEXHKremER"
  mavenCentral()
  mavenLocal()
  maven {
    name = "openlegacy-m2-public"
    url = uri("https://openlegacy.jfrog.io/openlegacy/ol-public")
    credentials {
      username = olUsername
      password = olPassword
    }
  }
  maven {
    name = "ol-3rd-party-libs"
    url = uri("https://openlegacy.jfrog.io/openlegacy/ol-3rd-party-libs")
    credentials {
      username = olUsername
      password = olPassword
    }
  }
}

dependencies {
  val openlegacyVersion: String by project
  implementation("com.ibm.icu:icu4j:71.1")
  implementation(enforcedPlatform("io.openlegacy:openlegacy-spring-bom:$openlegacyVersion"))
  implementation("io.openlegacy.springboot:properties-encryption-autoconfiguration:$openlegacyVersion")
  implementation("io.openlegacy.springboot:flow-spring-webflux-autoconfiguration:$openlegacyVersion")
  implementation("io.openlegacy.springboot:mf-cics-ts-autoconfiguration:$openlegacyVersion")
  implementation("org.springframework.boot:spring-boot-devtools")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
}

tasks {
  val javaVersion: String by project
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = javaVersion
    }
  }

  withType<JavaCompile> {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    options.encoding = "UTF-8"
  }

  named<Test>("test") {
    useJUnitPlatform()
  }

  jar {
    enabled = false
  }

}

// java {
  // If more than one JAR is built, the buildpack will not explode the runtime jar so live reload will not work.
  // Will this trigger a build?
//   withSourcesJar()
// }

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])
      versionMapping {
        usage("java-api") {
          fromResolutionOf("runtimeClasspath")
        }
        usage("java-runtime") {
          fromResolutionResult()
        }
      }
    }
  }
}
