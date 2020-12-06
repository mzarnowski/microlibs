version = "0.0.1"
group = "dev.mzarnowski.os.executable"

publishing.publications.withType<MavenPublication> {
    pom {
        name.set("Elf decoder")
        description.set("ELF decoder")
        url.set("https://github.com/mzarnowski/microlibs/os/executable/elf")
    }
}