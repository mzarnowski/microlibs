version = "0.0.5-SNAPSHOT"

apply(plugin = "org.jetbrains.kotlin.jvm")


dependencies {
    implementation(kotlin("stdlib"))
    "jmhImplementation"("com.lmax:disruptor:3.4.2")
}

publishing.publications.withType<MavenPublication> {
    pom {
        name.set("Buffer")
        description.set("Buffer supporting single writer and multiple readers")
        url.set("https://github.com/mzarnowski/microlibs/infra/buffer")
    }
}