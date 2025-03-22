package com.neko.decrypt;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;

import static com.neko.decrypt.MMK.*;

public class UnLocker {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                printWelcomeMessage();
                return;
            }

            String inputDir = "";
            String outputDir = "./output";

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--help":
                        printHelpMessage();
                        return;
                    case "-i":
                        if (i + 1 < args.length) {
                            inputDir = args[++i];
                        } else {
                            throw new IllegalArgumentException("You have not set the input directory");
                        }
                        break;
                    case "-o":
                        if (i + 1 < args.length) {
                            outputDir = args[++i];
                        } else {
                            throw new IllegalArgumentException("You have not set the output directory");
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Wrong parameters. If you want any help, type --help");
                }
            }

            processFiles(inputDir, outputDir);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void processFiles(String inputDir, String outputDir) throws IOException {
        Path srcPath = Path.of(inputDir);
        Path outPath = Path.of(outputDir);

        try (Stream<Path> lines = Files.list(srcPath)) {
            var iterator = lines.iterator();
            while (iterator.hasNext()) {
                Path path = iterator.next();
                try {
                    SecretKey secretKey = getSecretKey(path);
                    if (secretKey == null)
                        continue;
                    System.out.println("Processing: " + path + " " + secretKey);
                    handleDiv(path, outPath, secretKey);
                } catch (Exception e) {
                    System.out.println("Error: " + path + " -> " + e.getMessage());
                }
            }
        }
    }

    private static void printWelcomeMessage() {
        System.out.println("Welcome use the decrypt tool.");
        System.out.println("Usage : UnLocker [option]");
        System.out.println("Type --help to get help");
        System.out.println("Made time : 2025.3.22 Saturday 18:38:00");
    }

    private static void printHelpMessage() {
        System.out.println("-i [dir] : set input directory");
        System.out.println("-o [dir] : set output directory, it can be not set, and it will create a folder in your working directory");
    }

    public static void handleDiv(Path srcDiv, Path targetDiv, SecretKey secretKey) throws IOException {
        Files.walkFileTree(srcDiv, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relativize = srcDiv.getParent().relativize(dir);
                Path target = targetDiv.resolve(relativize);
                if (!Files.exists(target))
                    Files.createDirectory(target);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativize = srcDiv.getParent().relativize(file);
                Path target = targetDiv.resolve(relativize);

                if (!Files.exists(target))
                    switch (file.getFileName().toString()) {
                        case "fileMap.json", "meta.mko" -> {
                            Files.copy(file, target);
                        }
                        default -> {
                            byte[] data = secretKey.aes(Files.readAllBytes(file));
                            if (data == null) {
                                System.out.println("Warn: " + file + " Unable to decrypt this file");
                                break;
                            }
                            Files.write(target, data);
                        }
                    }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static MMK.SecretKey getSecretKey(Path rootPath) throws IOException {
        final Path[] files = new Path[3];
        Files.walkFileTree(rootPath, new FileVisitor<>() {
            @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".json")) {
                    if (isJson(Files.readAllBytes(file)))
                        return FileVisitResult.CONTINUE;
                    files[0] = file;
                    return FileVisitResult.TERMINATE;
                }
                if (file.toString().endsWith(".png") || file.toString().endsWith(".jpg")) {
                    if (isImage(Files.readAllBytes(file)))
                        return FileVisitResult.CONTINUE;
                    files[1] = file;
                    return FileVisitResult.TERMINATE;
                }
                if (file.toString().endsWith(".mp4")) {
                    if (isMp4(Files.readAllBytes(file)))
                        return FileVisitResult.CONTINUE;
                    files[2] = file;
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });

        if (files[0] != null) {
            return MMK.SecretKey.getFromJson(Files.readAllBytes(files[0]));
        } else if (files[1] != null) {
            return MMK.SecretKey.getFromImage(Files.readAllBytes(files[1]));
        } else if (files[2] != null) {
            return MMK.SecretKey.getFromMp4(Files.readAllBytes(files[2]));
        } else {
            throw new RuntimeException("Unable to judge");
        }
    }
}