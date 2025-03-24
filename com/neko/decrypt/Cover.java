package com.neko.decrypt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMover {

    public static void coverDir(Path srcPath, Path outPath) {
        try {
            moveFiles(srcPath, outPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void moveFiles(Path srcPath, Path outPath) throws IOException {
        // 删除srcPath内的所有文件与非空文件夹
        deleteDirectoryContents(srcPath);

        // 将outPath文件夹内所有文件与非空文件夹移到srcPath内
        moveDirectoryContents(outPath, srcPath);

        // 删除outPath文件夹
        Files.delete(outPath);
    }

    private static void deleteDirectoryContents(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                .sorted((a, b) -> b.compareTo(a)) // 先删除子文件/文件夹
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        }
    }

    private static void moveDirectoryContents(Path srcDir, Path destDir) throws IOException {
        if (Files.exists(srcDir)) {
            Files.walk(srcDir)
                .forEach(src -> {
                    Path dest = destDir.resolve(srcDir.relativize(src));
                    try {
                        Files.move(src, dest);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        }
    }
}