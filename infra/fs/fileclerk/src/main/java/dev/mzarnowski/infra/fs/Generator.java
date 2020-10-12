package dev.mzarnowski.infra.fs;

import java.nio.file.Path;

public interface Generator {
    void generate(Path output, String... arguments);
}
