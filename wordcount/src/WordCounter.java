import java.util.*;

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
    public List<Map.Entry<String, Integer>> getSortedWords() {
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(wordCount.entrySet());

        entryList.sort((e1,e2) -> Integer.compare(e2.getValue(), e1.getValue()));

        return entryList;
    }
    
    public int getTotalWords() {
        return totalWords;
    }
}