import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Cannot open file");
            return;
        }
        String filename = args[0];
        FileHandler fileHandler = new FileHandler(filename);
        WordCounter wordCounter = new WordCounter();
        try {
            fileHandler.processFile(
                    line -> line,
                    wordCounter::processLine
            );
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
            return;
        }
        List<WordStat> sortedWords = wordCounter.getSortedWords();
        WordFrequencyCsvWriter writer = new WordFrequencyCsvWriter();
        try {
            writer.processCSV(sortedWords, wordCounter.getTotalWords());
        } catch (IOException e) {
            System.err.println("Ошибка записи CSV: " + e.getMessage());
        }
    }
}