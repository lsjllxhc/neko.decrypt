package com.neko.GUI;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;
import static com.neko.decrypt.MMK.*;

public class UnLocker {

    // 动态参数方法（接收GUI传递的路径）
    public static void startDecrypt(Path srcPath, Path outPath) throws Exception {
        try (Stream<Path> lines = Files.list(srcPath)) {
            lines.forEach(path -> {
                try {
                    SecretKey secretKey = getSecretKey(path);
                    if (secretKey == null) return;
                    System.out.println("正在处理: " + path + " " + secretKey);
                    handleDiv(path, outPath, secretKey);
                } catch (Exception e) {
                    System.out.println("Error: " + path + " -> " + e.getMessage());
                }
            });
        }
    }

    /**
     * 处理目录
     *
     * @param srcDiv    源目录
     * @param targetDiv 目标目录
     * @param secretKey 密钥
     */
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
                                System.out.println("Warn: " + file + " 无法解密此文件");
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

    /**
     * 通过寻找json文件来尝试各个密钥
     *
     * @param rootPath 根目录
     * @return 最终密钥
     */
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