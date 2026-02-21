package validation.arguments;

import validation.config.ValidationConfig;
import validation.exceptions.InvalidArgumentException;

import java.io.File;

public class ArgumentsValidator {
    public static void validate(String[] args) throws InvalidArgumentException {
        if (args.length == 0) {
            throw new InvalidArgumentException("Файл не передан в аргументах.");
        }
        File file = new File(args[0]);
        if (!file.exists()) {
            throw new InvalidArgumentException("Файл не найден: " + args[0]);
        }
        if (!args[0].endsWith(ValidationConfig.ALLOWED_FILE_EXTENSION)) {
            throw new InvalidArgumentException(
                    "Файл должен иметь расширение " + ValidationConfig.ALLOWED_FILE_EXTENSION
            );
        }
    }
}