import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WordFrequencyCsvWriter {
    public void processCSV(List<WordStat> sortedWords,
                           int totalWords) throws IOException {
        try (FileWriter writer = new FileWriter("Result.csv")) {
            writer.append("Слово, Частота, Частота(в %)\n");
            for (WordStat stat : sortedWords) {
                double percent = (stat.getCount() * 100.0) / totalWords;
                writer.append(stat.getWord())
                        .append(",")
                        .append(String.valueOf(stat.getCount()))
                        .append(",")
                        .append(String.format(Locale.US, "%.2f", percent))
                        .append("%")
                        .append("\n");
            }
        }
    }
}
