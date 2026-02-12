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

        filehandler.processFile(wordCounter::processLine);
        List<Map.Entry<String, Integer>> sortedWords = wordCounter.getSortedWords();

        WriterCSV writer = new WriterCSV();
        writer.processCSV(sortedWords, wordCounter.getTotalWords());
    }
}