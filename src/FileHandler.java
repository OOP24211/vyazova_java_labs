import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class FileHandler {
    private String filename;
    public FileHandler(String filename) {
        this.filename = filename;
    }
    public void processFile(Consumer<String> lineProcessor) {
        try (FileInputStream fis = new FileInputStream(filename);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineProcessor.accept(line);
            }
        } catch (IOException e) {
            System.err.println("Error for reading file: " + e.getMessage());
        }
    }
}