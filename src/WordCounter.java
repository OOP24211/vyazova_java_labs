import java.util.*;

public class WordCounter {
    private static Map<String, Integer> wordCount = new HashMap<>();
    private static int totalWords = 0;

    public void processLine(String line) {
        StringBuilder sb = new StringBuilder();
        for (char ch : line.toCharArray()) {
            if (Character.isLetterOrDigit(ch)) {
                sb.append(ch);
            } else {
                if (sb.length() > 0) {
                    addWord(sb.toString());
                    sb.setLength(0);
                }
            }
        }
        if (sb.length() > 0) {
            addWord(sb.toString());
        }
    }

    private static void addWord(String word) {
        word = word.toLowerCase();
        wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        totalWords++;
    }
    public List<Map.Entry<String, Integer>> getSortedWords() {
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(wordCount.entrySet());
        entryList.sort((e1, e2) -> e2.getValue() - e1.getValue());
        return entryList;
    }

    public Map<String, Integer> getWordCount() {
        return wordCount;
    }

    public int getTotalWords() {
        return totalWords;
    }
}