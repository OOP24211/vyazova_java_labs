# CSV Word Counter

## Description
This Java project reads a `.txt` file, counts the frequency of each word, and outputs the results into a CSV file named `Result.csv`.

Words are processed using a regular expression (`\p{L}`, `\p{Nd}`) to keep only letters and digits, ignoring punctuation and special characters.

The generated CSV file contains:

- **Word** — the word itself  
- **Frequency** — the number of times the word appears  
- **Percentage** — the relative frequency of the word as a percentage of total words  

The project includes basic input validation:
- Checks that a filename argument is provided
- Checks that the file exists
- Ensures the file has a `.txt` extension

The project demonstrates:
- File input/output (UTF-8)
- Exception handling
- Maps and Lists
- Streams API
- Method references
- Basic validation logic

---

## Project Structure

- `Main.java` — entry point of the application  
- `FileHandler.java` — reads the text file line by line (UTF-8)  
- `WordCounter.java` — processes lines and counts word occurrences  
- `WordFrequencyCsvWriter.java` — writes sorted word statistics into `Result.csv`  
- `WordStat.java` — data class that stores word and count  
- `validation/` — contains argument and word validation logic  

---

## Usage

### 1. Compile the project

```bash
javac src/*.java src/validation/**/*.java
