import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class WordCounter {

    private final Map<String, Integer> wordCount = new HashMap<>();
    private int totalWords = 0;

    public void processLine(String line) {
        Arrays.stream(line.split("[^\\p{L}\\p{Nd}]+"))
                .filter(word -> !word.isEmpty())
                .forEach(this::addWord);
    }
    private void addWord(String word) {
        word = word.toLowerCase(Locale.ROOT);
        wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        totalWords++;
    }
    public List<WordStat> getSortedWords() {
        return wordCount.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(entry -> new WordStat(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
    public int getTotalWords() {
        return totalWords;
    }
    //для проверки слов на валидность
    public List<String> getAllWords() {
        return wordCount.keySet().stream().toList();
    }
}