rootProject.name = "microlibs"

include("infra:buffer")
findProject(":infra:buffer")?.name = "buffer"

include("infra:fs:fileclerk")
findProject(":infra:fs:fileclerk")?.name = "fileclerk"

include("infra:pki")
findProject(":infra:pki")?.name = "pki"

include("os:executable:elf")
findProject("os:executable:elf")?.name = "elf-executable"