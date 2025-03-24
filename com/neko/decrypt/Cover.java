package com.neko.decrypt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class Cover {

    public static void coverDir(String srcPath, String outPath) throws IOException {
        Path srcDir = Paths.get(srcPath);
        Path outDir = Paths.get(outPath);

        // 删除 srcPath 非空文件夹
        Files.walk(srcDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        // 重命名 outPath 非空文件夹为 srcPath
        Files.move(outDir, srcDir);
    }
}