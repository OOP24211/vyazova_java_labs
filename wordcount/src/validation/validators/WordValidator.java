package validation.validators;

import validation.config.ValidationConfig;

public class WordValidator {
    public static boolean isValidWord(String word) {
        if (word == null || word.isEmpty()) return false;
        if (word.length() > ValidationConfig.MAX_WORD_LENGTH) return false;
        return word.matches("[\\p{L}\\p{Nd}]+");
    }
}