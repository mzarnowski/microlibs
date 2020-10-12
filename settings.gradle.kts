rootProject.name = "microlibs"

include("infra:buffer")
findProject(":infra:buffer")?.name = "buffer"

include("infra:fs:fileclerk")
findProject(":infra:fs:fileclerk")?.name = "fileclerk"
