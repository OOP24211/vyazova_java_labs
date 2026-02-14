import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;

public class FileHandler {
    private String filename;
    public FileHandler(String filename) {
        this.filename = filename;
    }
    public <T> void processFile(Function<String, T> parser,
                                Consumer<T> processor) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                T parsedObject = parser.apply(line); //строка → T
                processor.accept(parsedObject);     // обработка T
            }
        }
    }
}

