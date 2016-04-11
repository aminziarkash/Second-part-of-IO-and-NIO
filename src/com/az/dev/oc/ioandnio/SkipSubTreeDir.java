package com.az.dev.oc.ioandnio;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class SkipSubTreeDir extends SimpleFileVisitor<Path> {
    
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        System.out.println("Pre\t:\t" + dir);
        String name = dir.getFileName().toString();
        if(name.equals("users")) {
            return FileVisitResult.SKIP_SUBTREE; // SKIP_SIBLINGS and TERMINATE
        }
        return FileVisitResult.CONTINUE;
    }
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        System.out.println("File\t:\t" + file);
        return FileVisitResult.CONTINUE;
    }
    
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        System.out.println("Post\t:\t" + dir);
        return FileVisitResult.CONTINUE;
    }
}
