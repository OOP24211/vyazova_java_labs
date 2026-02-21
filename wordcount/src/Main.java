import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Cannot open file");
            return;
        }
        String filename = args[0];
        FileHandler filehandler = new FileHandler(filename);
        WordCounter wordCounter = new WordCounter();

        try {
            filehandler.processFile(
                    line -> line,
                    wordCounter::processLine
            );
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
            return;
        }

        List<Map.Entry<String, Integer>> sortedWords = wordCounter.getSortedWords();

        WordFrequencyCsvWriter writer = new WordFrequencyCsvWriter();

        try {
            writer.processCSV(sortedWords, wordCounter.getTotalWords());
        } catch (IOException e) {
            System.err.println("Ошибка записи CSV: " + e.getMessage());
        }
    }
}