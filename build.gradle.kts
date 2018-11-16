import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

val local = Properties()
val localProperties: java.io.File = rootProject.file("local.properties")
if (localProperties.exists()) {
    localProperties.inputStream().use { local.load(it) }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.10"
    id("com.jfrog.bintray") version "1.8.1"
    `maven-publish`
    id("org.jetbrains.dokka") version "0.9.17"
}

defaultTasks("clean", "build")

group = "com.github.mvysny.vokdataloader"
version = "0.15-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // tests
    testCompile("com.github.mvysny.dynatest:dynatest-engine:0.12")
}

val java: JavaPluginConvention = convention.getPluginByName("java")

val sourceJar = task("sourceJar", Jar::class) {
    dependsOn(tasks.findByName("classes"))
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
}

val javadocJar = task("javadocJar", Jar::class) {
    val javadoc = tasks.findByName("dokka") as DokkaTask
    javadoc.outputFormat = "javadoc"
    javadoc.outputDirectory = "$buildDir/javadoc"
    dependsOn(javadoc)
    classifier = "javadoc"
    from(javadoc.outputDirectory)
}

publishing {
    publications {
        create("mavenJava", MavenPublication::class.java).apply {
            groupId = project.group.toString()
            this.artifactId = "vok-orm"
            version = project.version.toString()
            pom {
                description.set("VOK-DataLoader: The Paged/Filtered/Sorted DataLoader API")
                name.set("VoK-DataLoader")
                url.set("https://github.com/mvysny/vok-dataloader")
                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("mavi")
                        name.set("Martin Vysny")
                        email.set("martin@vysny.me")
                    }
                }
                scm {
                    url.set("https://github.com/mvysny/vok-dataloader")
                }
            }
            from(components.findByName("java")!!)
            artifact(sourceJar)
            artifact(javadocJar)
        }
    }
}

bintray {
    user = local.getProperty("bintray.user")
    key = local.getProperty("bintray.key")
    pkg(closureOf<BintrayExtension.PackageConfig> {
        repo = "github"
        name = "com.github.mvysny.vokdataloader"
        setLicenses("MIT")
        vcsUrl = "https://github.com/mvysny/vok-dataloader"
        publish = true
        setPublications("mavenJava")
        version(closureOf<BintrayExtension.VersionConfig> {
            this.name = project.version.toString()
            released = Date().toString()
        })
    })
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        // to see the exceptions of failed tests in Travis-CI console.
        exceptionFormat = TestExceptionFormat.FULL
    }
}
