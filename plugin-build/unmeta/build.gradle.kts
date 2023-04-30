import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    signing
    kotlin("jvm")
    `java-gradle-plugin`
    alias(libs.plugins.pluginPublish)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation(libs.asm)
    implementation(libs.androidGradlePlugin)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set(property("DISPLAY_NAME").toString())
                description.set(property("DESCRIPTION").toString())
                url.set(property("WEBSITE").toString())

                organization {
                    name.set(property("ORGANIZATION_NAME").toString())
                    url.set(property("ORGANIZATION_URL").toString())
                }

                licenses {
                    license {
                        name.set(property("LICENSE_NAME").toString())
                        url.set(property("LICENSE_URL").toString())
                    }
                }

                scm {
                    val (vcsHost, vcsUser, vcsRepo) = property("VCS_URL").toString()
                        .substringAfter("https://")
                        .split("/")
                    url.set(property("VCS_URL").toString())
                    connection.set("scm:git:git://$vcsHost/$vcsUser/$vcsRepo.git")
                    developerConnection.set("scm:git:ssh://git@$vcsHost:$vcsUser/$vcsRepo.git")
                }

                developers {
                    developer {
                        name.set(property("DEVELOPER_NAME").toString())
                    }
                }
            }
        }
    }

    repositories {
        maven {
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_SECRET")
            }
        }
    }
}

signing {
    val signingKey = System.getenv("GPG_SIGNING_KEY")
    val signingPassword = System.getenv("GPG_SIGNING_PASSWORD")
    useInMemoryPgpKeys(signingKey.chunked(64).joinToString("\n"), signingPassword)
    sign(tasks["jar"])
    sign(publishing.publications["mavenJava"])
}

gradlePlugin {
    plugins {
        create(property("ID").toString()) {
            id = property("ID").toString()
            implementationClass = property("IMPLEMENTATION_CLASS").toString()
            version = property("VERSION").toString()
            description = property("DESCRIPTION").toString()
            displayName = property("DISPLAY_NAME").toString()
            tags.set(listOf("kotlin", "metadata"))
        }
    }
}

gradlePlugin {
    website.set(property("WEBSITE").toString())
    vcsUrl.set(property("VCS_URL").toString())
}

tasks.create("setupPluginUploadFromEnvironment") {
    doLast {
        val key = System.getenv("GRADLE_PUBLISH_KEY")
        val secret = System.getenv("GRADLE_PUBLISH_SECRET")

        if (key == null || secret == null) {
            throw GradleException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
        }

        System.setProperty("gradle.publish.key", key)
        System.setProperty("gradle.publish.secret", secret)
    }
}
