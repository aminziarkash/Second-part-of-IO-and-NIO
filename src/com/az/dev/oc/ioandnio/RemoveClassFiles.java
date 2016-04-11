package com.az.dev.oc.ioandnio;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;


public class RemoveClassFiles extends SimpleFileVisitor<Path> {
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.getFileName().toString().endsWith(".class")) {
            Files.delete(file);
        }
        return FileVisitResult.CONTINUE; 
    }
    
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("home");
        System.out.println("Delete all '.class' files inside the directory tree\t:\t" + path + "\n");

        RemoveClassFiles removeClassFiles = new RemoveClassFiles();
        try {
            Files.walkFileTree(path, removeClassFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
