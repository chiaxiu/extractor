# Kotlin Public Declarations Extractor

This is a Kotlin script designed to extract and print all public declarations (functions, classes, properties, and objects) from Kotlin source files in a given directory. It utilises the `kotlin-compiler-embeddable` to parse and analyse the source code.

## Prerequisites

Ensure you have the following installed:

- Kotlin (for executing the Kotlin script)
- Internet access (for downloading the dependencies)

## Usage

1. Save the script to a file. For example, `extractor.main.kts`.
2. Make the script executable on your system (optional, depending on your environment):
    ```
    chmod +x extractor.main.kts
    ```
3. Run the script by providing a source directory containing Kotlin files:
    ```
   ./extractor.main.kts <source_directory>
   ```
4. The output will be printed to the console, showing all public declarations detected in the Kotlin files.

## How It Works

1. **Scans the Provided Directory**

    - The script recursively searches for `kt` files within the given directory.
   
2. **Parse the Kotlin Files**

    - Uses `kotlin-compiler` and `kotlin-compiler-embeddable` to parse the files.

3. **Extract Public Declarations**:

    - Identifies and prints public functions, classes, properties, and objects.



