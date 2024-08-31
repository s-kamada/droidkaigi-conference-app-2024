package io.github.droidkaigi.confsched.primitive

import com.google.devtools.ksp.gradle.KspTaskNative
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KmpPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")

            }
            tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
                kotlinOptions.jvmTarget = "11"
            }

            // https://github.com/DroidKaigi/conference-app-2024/issues/485#issuecomment-2304251937
            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink>().configureEach {
                notCompatibleWithConfigurationCache("Configuration chache not supported for a system property read at configuration time")
            }
            tasks.withType<KspTaskNative>().configureEach {
                notCompatibleWithConfigurationCache("Configuration chache not supported for a system property read at configuration time")
            }

            kotlin {
                with(sourceSets) {
                    commonMain {
                        dependencies {
                            implementation(libs.findLibrary("kermit").get())
                        }
                    }
                }
                compilerOptions {
                    freeCompilerArgs.add("-Xcontext-receivers")
                }
            }
        }
    }
}

fun Project.kotlin(action: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure(action)
}
