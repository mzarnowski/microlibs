version = "0.0.5-SNAPSHOT"

publishing.publications.withType<MavenPublication> {
    pom {
        name.set("File Clerk")
        description.set("Library for generating files from command line")
        url.set("https://github.com/mzarnowski/microlibs/infra/fs/fileclerk")
    }
}