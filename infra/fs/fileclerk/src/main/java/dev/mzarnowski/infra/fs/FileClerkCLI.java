package dev.mzarnowski.infra.fs;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileClerkCLI {
    private static final Class<Generator> GeneratorType = Generator.class;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Please, specify generator class and output directory:");
            System.err.println("fileclerk GENERATOR_CLASS OUTPUT [-- [ARG]...]");
            System.exit(1);
        }

        try {
            run(args);
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Could not generate files: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void run(String[] args) {
        var generator = parseGenerator(args[0]);
        Path output = parseOutput(args[1]);
        var arguments = Arrays.stream(args).skip(3).toArray(String[]::new);

        generator.generate(output, arguments);
    }

    private static Path parseOutput(String arg) {
        var path = Paths.get(arg);
        if (Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Output path is a file: " + path);
        }
        return path;
    }

    private static Generator parseGenerator(String arg) {
        try {
            var generatorClass = Class.forName(arg);
            if (!GeneratorType.isAssignableFrom(generatorClass)) {
                throw new IllegalArgumentException("Not a generator: " + generatorClass + " is not inheriting from " + GeneratorType);
            }

            return (Generator) generatorClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class unavailable: " + arg);
        } catch (Exception e) {
            throw new IllegalStateException("Could not instantiate runner: " + e.getMessage());
        }
    }

}
