package com.az.dev.oc.ioandnio;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class MyPathMatcher extends SimpleFileVisitor<Path>{

    private PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/password/**.txt"); // ** means any subdirectory

    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (matcher.matches(file)) {
            System.out.println(file);
        }
        return FileVisitResult.CONTINUE;
    }
}
