package com.az.dev.oc.ioandnio;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

public class App {
    
    // create a date
    private Date januaryFirst = new GregorianCalendar(2013, Calendar.JANUARY, 1).getTime();

    public static void main(String[] args) {
        
        App application = new App();
        
        // DIRECTORY AND FILE ATTRIBUES
        application.readingAndWritingAttributesTheEasyWay();
        
        application.workingWithBasicFileAttributes();
        
        application.workingWithDosFileAttributes();
        
        application.workingWithPosixFileAttributes();
        
        // DIRECTORYSTREAM
        application.workingWithDirectoryStream();
        
        // FILEVISITOR
        application.workingWithFileVisitor();
        
        // PATHMATCHER
        application.workingWithPathMatcher();
        
        // WATCHSERVICE
        application.workingWithWatchService();
        
    }
    
    private void workingWithWatchService() {
        addHeaderSeparator();
        System.err.println("WORKING WITH WatchService CLASS\n");

        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get("DeleteSubDirsInThisDir");
            dir.register(watcher, ENTRY_DELETE); // ENTRY_CREATE, ENTRY_MODIFY can also be added to listen for those events
            while (true) {
                WatchKey key;
                try {
                    System.err.println("[WARNING] The directory is not empty\nPlease MANUALLY empty the '" + dir.getFileName() + "' directory in order to proceed");
                    key = watcher.take(); // watcher.poll(5, TimeUnit.SECONDS) can be used to continue after 5 sec anyway
                } catch (InterruptedException e) {
                    return;
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    System.out.println("Name\t:\t" + kind.name());
                    System.out.println("Type\t:\t" + kind.type());
                    System.out.println("Context\t:\t" + event.context());
                    
                    String name = event.context().toString();
                    if (name.equals("directoryToDelete")) {
                        System.out.format("Directory deleted, now we can proceed");
                        addFooterSeparator();
                        return;
                    }
                }
                key.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void workingWithPathMatcher() {
        System.err.println("WORKING WITH PathMatcher CLASS\n");

        addHeaderSeparator();
        
        simplePathMatcherExample();
        
        printFileNamesFromPasswordDirectory();
   
        addFooterSeparator();
    }
    
    private void printFileNamesFromPasswordDirectory() {
        System.out.println("Print file names with their dirs that are inside the 'password' directory\n");
        MyPathMatcher dirs = new MyPathMatcher();
        try {
            Files.walkFileTree(Paths.get(""), dirs); // start with root
        } catch(IOException e) {
            e.printStackTrace();
        }
        addSubSeparator();
    }
    
    private void simplePathMatcherExample() {
        Path path1 = Paths.get("home/One.txt");
        Path path2 = Paths.get("One.txt");
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.txt"); // '*' excluding directory boundary '**' including
        
        String path1Name = path1.getParent() + "\\" + path1.getFileName();
        String path2Name = path2.getFileName().toString();
        System.out.println("Path 1\t:\t" + path1Name);
        System.out.println("Path 2\t:\t" + path2Name);
        
        System.out.println("glob:*.txt on Path 1\t:\t" + matcher.matches(path1));
        System.out.println("glob:*.txt on Path 2\t:\t" + matcher.matches(path2));                                                                                                                            
    }
    
    private void workingWithFileVisitor() {
        addHeaderSeparator();
        
        System.err.println("WORKING WITH FileVisitor CLASS\n");
        
        simpleFileVisitorExample();
        
        anotherFileVisitorExample();
        
        skipSubTreeFileVisitorExample();
        
        addFooterSeparator();
    }
    
    private void simpleFileVisitorExample() {
        Path path = Paths.get("home");
        System.out.println("Delete all '.class' files inside the directory tree\t:\t" + path + "\n");

        RemoveClassFiles removeClassFiles = new RemoveClassFiles();
        try {
            Files.walkFileTree(path, removeClassFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addSubSeparator();
    }
    
    private void anotherFileVisitorExample() {
        Path path = Paths.get("home");
        System.out.println("Printing the directories and the contents of the directory\t:\t" + path.getFileName() + "\n");
        
        PrintDirs printDirs = new PrintDirs();
        try {
            Files.walkFileTree(path, printDirs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addSubSeparator();
    }
    
    private void skipSubTreeFileVisitorExample() {
        Path path = Paths.get("home");
        System.out.println("Printing the contents of the directory, but skipping the 'users' directory\t:\t" + path.getFileName() + "\n");
        
        SkipSubTreeDir skipSubTreeDir = new SkipSubTreeDir();
        try {
            Files.walkFileTree(path, skipSubTreeDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addSubSeparator();
    }
    
    private void workingWithDirectoryStream() {
        addHeaderSeparator();
        
        System.err.println("WORKING WITH DirectoryStream CLASS\n");
        
        basicDirectoryStreamExample();
        
        advancedDirectoryStreamExample();
        
        addFooterSeparator();
    }
    
    private void advancedDirectoryStreamExample() {
        Path dir = Paths.get("home/users");
        System.out.println("Using the DirectoryStream with 'glob pattern' argument to search only for certain users (in this example \nusers that start with 'a' or 'v')\n");
        System.out.println("Directory\t:\t" + dir.getParent() + "\\" + dir.getFileName() + "\n");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "[av]*")) { // HERE WE PASS THE GLOB PATTERN ARGUMENT
            for (Path path : stream) {
                System.out.println("Found user\t:\t" + path.getFileName());
            }
            System.out.println("No more users...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void basicDirectoryStreamExample() {
        Path dir = Paths.get("home/users");
        System.out.println("Suppose we have a users-directory and we want to loop through it to get all the users\n('dir' in Windows and 'ls' in LINUX)...\n");
        System.out.println("We could use DirectoryStream to loop through\n");
        System.out.println("Directory\t:\t" + dir.getParent() + "\\" + dir.getFileName() + "\n");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                System.out.println("Found user\t:\t" + path.getFileName());
            }
            System.out.println("No more users...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        addSubSeparator();
    }
    
    private void workingWithPosixFileAttributes() {
        addHeaderSeparator();
        
        System.err.println("WORKING WITH PosixFileAttributes CLASS\n");
        
        Path path = Paths.get("Working With PosixFileAttributes File");
        
        // NOT SUPPORTED IN WINDOWS
        try {
            Files.createFile(path);
            PosixFileAttributes posix = Files.readAttributes(path, PosixFileAttributes.class);
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rw-r--r--");
            Files.setPosixFilePermissions(path, permissions);
            System.out.println(posix.permissions());
            System.out.println(posix.group());
        } catch(IOException | UnsupportedOperationException e) {
            System.out.println("PosixFileAttributes are not supported in Windows.");
        } finally {
            try {
                Files.delete(path);                
            } catch(IOException e) {
                System.out.println("File cannot be deleted.");
            }
        }
        
        addSubSeparator();
        
        getFileOwner(path);
        
        addFooterSeparator();
    }
    
    private void getFileOwner(Path path) {
        System.out.println("Get file owner using Files.getOwner(path)...\n");
        try {
            Files.createFile(path);
            System.out.println("File owner\t:\t" + Files.getOwner(path));
            Files.delete(path);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private void workingWithDosFileAttributes() {
        addHeaderSeparator();
        
        System.err.println("WORKING WITH DosFileAttributes CLASS\n");
        
        Path path = Paths.get("Working with DosFileAttributes File");
        try{
            System.out.println("Creating file\t:\t" + path.getFileName());
            Files.createFile(path);
            System.out.println("\nSetting the file attributes 'hidden' and 'readonly' to true...\n");
            Files.setAttribute(path, "dos:hidden", true);
            Files.setAttribute(path, "dos:readonly", true);
            DosFileAttributes dos = Files.readAttributes(path, DosFileAttributes.class);
            System.out.println("Check if they are set correctly...");
            System.out.println("Is hidden\t:\t" + dos.isHidden());
            System.out.println("Is read-only\t:\t" + dos.isReadOnly());
            
            System.out.println("\nSetting the file attributes 'hidden' and 'readonly' to false...");
            Files.setAttribute(path, "dos:hidden", false);
            Files.setAttribute(path, "dos:readonly", false);
            dos = Files.readAttributes(path, DosFileAttributes.class);
            System.out.println("\nCheck after setting...");
            System.out.println("Is hidden\t:\t" + dos.isHidden());
            System.out.println("Is read-only\t:\t" + dos.isReadOnly());
            
            System.out.println("\nDeleting file '" + path.getFileName() + "'");
            Files.delete(path);
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        addFooterSeparator();
    }
    
    private void workingWithBasicFileAttributes() {
        addHeaderSeparator();
        
        System.err.println("WORKING WITH BasicFileAttributes CLASS\n");

        // Path path = Paths.get("src\\com\\az\\dev\\oc\\ioandnio");
        // Path path = Paths.get("src/com/az/dev/oc/ioandnio");
        Path path = Paths.get("src", "com", "az", "dev", "oc", "ioandnio");

        System.out.println("Directory\t:\t" + path.getParent() + "\\" + path.getFileName());
        try {
            BasicFileAttributes basic = Files.readAttributes(path, BasicFileAttributes.class);
            System.out.println("Creation time\t:\t" + basic.creationTime());
            System.out.println("Last acccessed\t:\t" + basic.lastAccessTime());
            System.out.println("Last mod. date\t:\t" + basic.lastModifiedTime());
            System.out.println("Is directory\t:\t" + basic.isDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        addSubSeparator();
        
        updateLastAccessedTime(path);
        
        addFooterSeparator();
    }
    
    private void updateLastAccessedTime(Path path) {
        System.out.println("Updating last accessed time using 'BasicFileAttributeView' (NOT ON THE EXAM) ...\n");
        
        System.out.println("File/Directory name\t:\t" + path.getFileName() + "\n");
        try {
            BasicFileAttributes basic = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime lastUpdated = basic.lastModifiedTime();
            FileTime created = basic.creationTime();
            FileTime now = FileTime.fromMillis(System.currentTimeMillis());
            System.out.println("Last updated\t\t:\t" + lastUpdated);
            System.out.println("Created\t\t\t:\t" + created);
            System.out.println("Now\t\t\t:\t" + now);
            BasicFileAttributeView basicView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
            System.out.println("\nUsing 'basicView.setTimes(lastUpdated, now, created)' to update the last accessed time...");
            basicView.setTimes(lastUpdated, now, created);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        addSubSeparator();
        System.out.println("Things to remember!\n");
        System.out.println("xxxFileAttributes\t:\tClasses are read-only");
        System.out.println("xxxFileAttributeView\t:\tClasses allow updates");
    }

    private void readingAndWritingAttributesTheEasyWay() {
        addHeaderSeparator();
        
        System.err.println("READING AND WRITING ATTRIBUTES THE EASY WAY EXAMPLE\n");

        // create 'Last modified dir' directory
        Path lastModifiedDir = createLastModifiedDirectory();
        
        readAndWriteTheOldWay();
        
        readAndWriteTheNewWay();
        
        try {
            System.out.println("Deleting '" + lastModifiedDir.getFileName() + "' directory");
            Files.delete(lastModifiedDir); // delete the 'Last modified dir' directory
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        addFooterSeparator();
    }
    
    private void readAndWriteTheOldWay() {
        System.out.println("Reading and writing file the OLD way...\n");
        
        // old way
        File file = new File("Last modified dir/file");
        try {
            System.out.println("Creating file\t\t\t:\t" + file.getName());
            file.createNewFile();
            System.out.println("Modified date BEFORE setting it\t:\t" + file.lastModified());
            file.setLastModified(januaryFirst.getTime()); // set the last modified date
            System.out.println("Modified date AFTER setting it\t:\t" + file.lastModified());
            System.out.println("Deleting file\t\t\t:\t" + file.getName());
            file.delete();
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        addSubSeparator();
    }
    
    private void readAndWriteTheNewWay() {
        System.out.println("Reading and writing file the NEW way...\n");
        
        // new way
        Path path = Paths.get("Last modified dir/file2");
        FileTime fileTime = FileTime.fromMillis(januaryFirst.getTime());
        try {
            Files.createFile(path);
            System.out.println("Created file\t\t:\t" + path.getFileName());
            System.out.println("Get last modified date\t:\t" + Files.getLastModifiedTime(path));
            Files.setLastModifiedTime(path, fileTime);
            System.out.println("Set last modified date\t:\t" + Files.getLastModifiedTime(path));

            addSubSeparator();
            // print file permissions
            printFilePermissions(path);
            
            System.out.println("\nFile '" + path.getFileName() + "' deleted");
            Files.delete(path);
        } catch(IOException e) {
            e.printStackTrace();
        }
        addSubSeparator();
    }
    
    private void printFilePermissions(Path path) {
        System.out.println("File permission for\t:\t" + path.getFileName());
        System.out.println("\nFile is executable\t:\t" + Files.isExecutable(path));
        System.out.println("File is readable\t:\t" + Files.isReadable(path));
        System.out.println("File is writable\t:\t" + Files.isWritable(path));
    }
    
    private Path createLastModifiedDirectory() {
        // create directory 'Last modified dir'
        Path lastModifiedDir = Paths.get("Last modified dir");
        try {
            System.out.println("Creating directory\t:\t" + lastModifiedDir.getFileName());
            Files.createDirectory(lastModifiedDir);
        } catch(IOException e) {
            e.printStackTrace();
        }        
        addSubSeparator();
        return lastModifiedDir;
    }
    
    private void addSubSeparator() {
        System.out.println("-----------------------------------------------------------------------------------------------------");
    }
    
    private void addFooterSeparator() {
        System.out.println("*****************************************************************************************************");
    }
    
    private void addHeaderSeparator() {
        System.out.println("\n\n\n*****************************************************************************************************");
    }
}
