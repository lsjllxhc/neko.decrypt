package com.neko.GUI;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.*;
import java.util.stream.Stream;

import static com.neko.decrypt.MMK.*;

public class UnLocker {

    private static final Logger logger = Logger.getLogger(UnLocker.class.getName());

    static {
        try {
            LogManager.getLogManager().reset();
            FileHandler fileHandler = new FileHandler("log.txt", false); // 'false' to overwrite the file
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize logger", e);
        }
    }

    public static void startDecrypt(Path srcPath, Path outPath) throws Exception {
        try (Stream<Path> paths = Files.walk(srcPath)) {
            paths.filter(Files::isRegularFile)
                 .forEach(path -> {
                     try {
                         SecretKey secretKey = getSecretKey(path);
                         if (secretKey != null) {
                             logger.log(Level.INFO, "正在处理: {0} {1}", new Object[]{path, secretKey});
                             handleDiv(path, outPath, secretKey);
                         }
                     } catch (Exception e) {
                         logger.log(Level.SEVERE, "Error processing file: " + path, e);
                     }
                 });
        }
    }

    public static void handleDiv(Path srcDiv, Path targetDiv, SecretKey secretKey) throws IOException {
        Files.walkFileTree(srcDiv, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path target = targetDiv.resolve(srcDiv.getParent().relativize(dir));
                if (!Files.exists(target)) {
                    Files.createDirectory(target);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path target = targetDiv.resolve(srcDiv.getParent().relativize(file));
                if (!Files.exists(target)) {
                    switch (file.getFileName().toString()) {
                        case "fileMap.json", "meta.mko":
                            Files.copy(file, target);
                            break;
                        default:
                            byte[] data = secretKey.aes(Files.readAllBytes(file));
                            if (data != null) {
                                Files.write(target, data);
                            } else {
                                logger.log(Level.WARNING, "Warn: {0} 无法解密此文件", file);
                            }
                            break;
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static MMK.SecretKey getSecretKey(Path rootPath) throws IOException {
        final Path[] files = new Path[3];
        Files.walkFileTree(rootPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.toString();
                if (fileName.endsWith(".json") && !isJson(Files.readAllBytes(file))) {
                    files[0] = file;
                    return FileVisitResult.TERMINATE;
                }
                if ((fileName.endsWith(".png") || fileName.endsWith(".jpg")) && !isImage(Files.readAllBytes(file))) {
                    files[1] = file;
                    return FileVisitResult.TERMINATE;
                }
                if (fileName.endsWith(".mp4") && !isMp4(Files.readAllBytes(file))) {
                    files[2] = file;
                    return FileVisitResult.TERMINATE;
                }
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
            throw new RuntimeException("无法判断");
        }
    }
}