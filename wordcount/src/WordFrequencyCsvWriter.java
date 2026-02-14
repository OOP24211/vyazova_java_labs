import java.util.*;
import java.io.*;

public class WordFrequencyCsvWriter {
    public void processCSV(List<Map.Entry<String, Integer>> sortedWords, int totalWords) {

        try (FileWriter writer = new FileWriter("Result.csv")) { // файл создается автоматически
            writer.append("Слово, Частота, Частота(в %)\n");

            for (Map.Entry<String, Integer> entry : sortedWords) {
                String word = entry.getKey();
                int freq = entry.getValue();
                double percent = (freq * 100.0) / totalWords;

                writer.append(word)
                        .append(",")
                        .append(String.valueOf(freq))
                        .append(",")
                        .append(String.format(Locale.US, "%.2f", percent))
                        .append("%")
                        .append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
