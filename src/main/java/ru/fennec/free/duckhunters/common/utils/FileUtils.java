package ru.fennec.free.duckhunters.common.utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

public class FileUtils {

    public static void copyDirectory(Path source, Path target, CopyOption... options) {
        try {
            Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Files.createDirectories(target.resolve(source.relativize(dir)));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, target.resolve(source.relativize(file)), options);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ex) {
            String pathSource = getPathName(source);
            String pathTarget = getPathName(target);
            throw new RuntimeException(
                    String.format("Failed to copy directory '%s' to '%s'",
                            pathSource, pathTarget), ex);
        }
    }

    public static String getPathName(Path path) {
        return path.toString().replace('\\', '/');
    }

    public static void deleteDirectory(Path dir) {
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ex) {
            String path = getPathName(dir);
            throw new RuntimeException("Failed to delete directory: " + path, ex);
        }
    }

}
