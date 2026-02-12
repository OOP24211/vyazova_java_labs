# CSV Word Counter

## Description
This Java project reads a text file, counts the frequency of each word, and outputs the results into a CSV file named `Result.csv`.  
Words are processed using `Character.isLetterOrDigit` to ensure proper separation, ignoring punctuation and special characters. The CSV includes:

- **Word** — the word itself  
- **Frequency** — the number of times the word appears  
- **Percentage** — the relative frequency of the word as a percentage of total words  

The project demonstrates working with file input/output, maps, lists, and method references in Java.

---

## Project Structure

- `Main.java` — the main class that orchestrates the file processing and CSV output  
- `FileHandler.java` — handles reading the text file line by line  
- `WordCounter.java` — processes each line, counts word occurrences  
- `WriterCSV.java` — writes the sorted word count and percentages into `Result.csv`  

---

## Usage

1. Compile all Java files:
   ```bash
   javac src/*.java


Run the program with a text file as an argument:
"java -cp src Main Text.txt"
After running, a CSV file Result.csv will be created in the project directory.
