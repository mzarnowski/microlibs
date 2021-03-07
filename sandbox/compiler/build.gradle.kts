version = "0.0.1-SNAPSHOT"

apply(plugin = "org.jetbrains.kotlin.jvm")

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("com.h2database:h2:1.4.200")
}