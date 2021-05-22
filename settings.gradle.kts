rootProject.name = "microlibs"

include("infra:buffer")
findProject(":infra:buffer")?.name = "buffer"

include("infra:fs:fileclerk")
findProject(":infra:fs:fileclerk")?.name = "fileclerk"

include("infra:io:binary")
findProject(":io:binary")?.name = "binary-io"

include("os:executable:elf")
findProject("os:executable:elf")?.name = "elf-executable"