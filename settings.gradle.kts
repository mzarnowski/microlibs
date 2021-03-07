rootProject.name = "microlibs"

include("infra:buffer")
findProject(":infra:buffer")?.name = "buffer"

include("infra:fs:fileclerk")
findProject(":infra:fs:fileclerk")?.name = "fileclerk"

include("sandbox:compiler")
findProject(":sandbox:compiler")?.name = "compiler"

include("os:executable:elf")
findProject("os:executable:elf")?.name = "elf-executable"