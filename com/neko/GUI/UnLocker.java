package com.neko.GUI;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Stream;
import static com.neko.decrypt.MMK.*;

public class UnLocker {
    private static final Logger LOGGER = Logger.getLogger(UnLocker.class.getName());

    static {
        try {
            // 创建 FileHandler 并设置日志文件路径
            Path logDir = Path.of("logs");
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            FileHandler fileHandler = new FileHandler(logDir.resolve("unLocker.log").toString(), true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            // 重定向 System.out 和 System.err
            System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    LOGGER.info(String.valueOf((char) b));
                }
            }));
            System.setErr(new java.io.PrintStream(new java.io.OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    LOGGER.severe(String.valueOf((char) b));
                }
            }));
        } catch (IOException e) {
            LOGGER.severe("无法创建日志文件处理程序: " + e.getMessage());
        }
    }

    public static void startDecrypt(Path srcPath, Path outPath) throws Exception {
        try (Stream<Path> lines = Files.list(srcPath)) {
            lines.forEach(path -> {
                try {
                    SecretKey secretKey = getSecretKey(path);
                    if (secretKey == null) return;
                    LOGGER.info("正在处理: " + path + " " + secretKey);
                    handleDiv(path, outPath, secretKey);
                } catch (Exception e) {
                    LOGGER.severe("Error: " + path + " -> " + e.getMessage());
                }
            });
        }
    }

    public static void handleDiv(Path srcDiv, Path targetDiv, SecretKey secretKey) throws IOException {
        Files.walkFileTree(srcDiv, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path target = targetDiv.resolve(srcDiv.getParent().relativize(dir));
                if (!Files.exists(target)) Files.createDirectory(target);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path target = targetDiv.resolve(srcDiv.getParent().relativize(file));
                if (!Files.exists(target)) {
                    switch (file.getFileName().toString()) {
                        case "fileMap.json", "meta.mko" -> Files.copy(file, target);
                        default -> {
                            byte[] data = secretKey.aes(Files.readAllBytes(file));
                            if (data == null) {
                                LOGGER.warning("Warn: " + file + " 无法解密此文件");
                                break;
                            }
                            Files.write(target, data);
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static SecretKey getSecretKey(Path rootPath) throws IOException {
        final Path[] files = new Path[3];
        Files.walkFileTree(rootPath, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".json") && !isJson(Files.readAllBytes(file))) {
                    files[0] = file;
                    return FileVisitResult.TERMINATE;
                }
                if ((file.toString().endsWith(".png") || file.toString().endsWith(".jpg")) && !isImage(Files.readAllBytes(file))) {
                    files[1] = file;
                    return FileVisitResult.TERMINATE;
                }
                if (file.toString().endsWith(".mp4") && !isMp4(Files.readAllBytes(file))) {
                    files[2] = file;
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });

        if (files[0] != null) {
            return SecretKey.getFromJson(Files.readAllBytes(files[0]));
        } else if (files[1] != null) {
            return SecretKey.getFromImage(Files.readAllBytes(files[1]));
        } else if (files[2] != null) {
            return SecretKey.getFromMp4(Files.readAllBytes(files[2]));
        } else {
            throw new RuntimeException("无法判断");
        }
    }
}