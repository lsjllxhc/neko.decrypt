package com.neko.decrypt;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

import static com.neko.decrypt.MMK.*;

public class UnLocker {
    public static void main(String[] args) throws Exception {

        String inputDir = "";
        String outputDir = "";

        if (args.length == 0) {
            System.out.println("Welcome use the decrypt tool.");
            System.out.println("Usage : UnLocker [option]");
            System.out.println("Type --help to get help");
            System.out.println("Made time : 2025.3.22 Saturday");
            return;
        }

        else if (args.length == 1) {
            if (args[0].equals("--help")){
                System.out.println("-i [dir] : set input directory");
                System.out.println("-o [dir] : set output directory,it can be not set,and it will create a folder in your working directory");
                return;
            }
            else if (args[0].equals("-i")){
                System.out.println("You have not set the directory");
                return;
            }
            else if (args[0].equals("-o")){
                System.out.println("You must set input directory first.");
                return;
            }
            else {
                System.out.println("Wrong parameters.");
                System.out.println("If you want any help,type --help");
                return;
            }
        }

        else if (args.length == 2) {
            if (args[0].equals("-o")) {
                System.out.println("You must set input directory first.");
                return;
            }
            else if (args[0].equals("-i")) {
                inputDir = args[1];
                outputDir = "./output";
            }
            else{
                System.out.println("Wrong parameters.");
                System.out.println("If you want any help,type --help");
                return;
            }
        }

        else if (args.length == 3){
            if (args[0].equals("-o")) {
                System.out.println("You must set input directory first.");
                return;
            }
            else if (args[0].equals("-i")) {
                if (args[2].equals("-o")){
                    System.out.println("You have not set output directory");
                    return;
                }
                else{
                    System.out.println("Wrong parameters.");
                    System.out.println("If you want any help,type --help");
                    return;
                }
            }
            else{
                System.out.println("Wrong parameters.");
                System.out.println("If you want any help,type --help");
                return;
            }
        }

        else if (args.length == 4){
            if (args[0].equals("-o")) {
                System.out.println("You must set input directory first.");
                return;
            }
            else if (args[0].equals("-i")) {
                if (args[2].equals("-o")){
                    inputDir = args[1];
                    outputDir = args[3];
                }
                else{
                    System.out.println("Wrong parameters.");
                    System.out.println("If you want any help,type --help");
                    return;
                }
            }
            else{
                System.out.println("Wrong parameters.");
                System.out.println("If you want any help,type --help");
                return;
            }
        }

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
                    System.out.println("正在处理: " + path + " " + secretKey);
                    handleDiv(path, outPath, secretKey);
                } catch (Exception e) {
                    System.out.println("Error: " + path + " -> " + e.getMessage());
                }
            }
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
                                System.out.println("Warn: " + file + " 无法解密此文件");
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

    /**
     * 通过寻找json文件来尝试各个密钥
     *
     * @param rootPath 根目录
     * @return 最终密钥
     */
    public static MMK.SecretKey getSecretKey(Path rootPath) throws IOException {
        final Path[] files = new Path[3];
        Files.walkFileTree(rootPath, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
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
            throw new RuntimeException("无法判断");
        }
    }
}