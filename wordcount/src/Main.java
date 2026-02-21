import validation.arguments.ArgumentsValidator;
import validation.exceptions.InvalidArgumentException;
import validation.validators.WordValidator;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            ArgumentsValidator.validate(args);

            String filename = args[0];
            FileHandler fileHandler = new FileHandler(filename);
            WordCounter wordCounter = new WordCounter();

            fileHandler.processFile(
                    line -> line,
                    wordCounter::processLine
            );
            for (String word : wordCounter.getAllWords()) {
                if (!WordValidator.isValidWord(word)) {
                    System.out.println("Невалидное слово: " + word);
                }
            }

            WordFrequencyCsvWriter writer = new WordFrequencyCsvWriter();
            writer.processCSV(wordCounter.getSortedWords(), wordCounter.getTotalWords());

        } catch (InvalidArgumentException e) {
            System.err.println("Ошибка аргументов: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка ввода/вывода: " + e.getMessage());
        }
    }
}