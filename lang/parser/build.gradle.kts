version = "0.0.1-SNAPSHOT"
group = "dev.mzarnowski.lang.parser"

apply(plugin = "org.jetbrains.kotlin.jvm")


dependencies {
    implementation(kotlin("stdlib"))
}

publishing.publications.withType<MavenPublication> {
    pom {
        name.set("Parser")
        description.set("Parser")
        url.set("https://github.com/mzarnowski/microlibs/lang/parser")
    }
}