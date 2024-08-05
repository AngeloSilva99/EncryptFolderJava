import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class DeleteHiddenFiles {

    public static void DeleteFiles() {

        Path startPath = Paths.get(MainWindow.textField.getText());

        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    deleteIfMatches(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    deleteIfMatches(dir);
                    return FileVisitResult.CONTINUE;
                }

                private void deleteIfMatches(Path path) throws IOException {
                    String fileName = path.getFileName().toString();
                    if (Files.isHidden(path) || fileName.startsWith("._") || fileName.startsWith(".DS_Store") || !fileName.startsWith(".Trashes") || !fileName.startsWith(".Spotlight-V100")) {
                        System.out.println("Deleting: " + path);
                        Files.delete(path);
                    }
                }
            });
        } catch (IOException e) {
           	System.out.println(e);
        }
    }
}