package com.neko.decrypt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileMover {
    private static final Logger LOGGER = Logger.getLogger(FileMover.class.getName());

    public static void coverDir(Path srcPath, Path outPath) {
        try {
            moveFiles(srcPath, outPath);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error covering directory", e);
        }
    }

    public static void moveFiles(Path srcPath, Path outPath) throws IOException {
        // 删除srcPath内的所有文件与非空文件夹
        deleteDirectoryContents(srcPath);

        // 将outPath文件夹内所有文件与非空文件夹移到srcPath内
        moveDirectoryContents(outPath, srcPath);

        // 删除outPath文件夹
        Files.deleteIfExists(outPath);
    }

    private static void deleteDirectoryContents(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                .sorted(Comparator.reverseOrder()) // 先删除子文件/文件夹
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Error deleting file: " + p, e);
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
                        if (Files.exists(dest)) {
                            LOGGER.log(Level.WARNING, "Target file already exists: " + dest);
                        }
                        Files.move(src, dest);
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Error moving file: " + src + " to " + dest, e);
                    }
                });
        }
    }
}