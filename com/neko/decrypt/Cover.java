package com.neko.decrypt;

public class Cover{
    public static void coverDir(Path srcPath, Path outPath) throws IOException{
        if (Files.exists(srcPath)) {
            try (Stream<Path> walk = Files.walk(srcPath)) {
                walk.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } 
                        catch (IOException e) {
                            throw new RuntimeException("Failed to delete " + path, e);
                        }
                    });
            }
        }
    }
}