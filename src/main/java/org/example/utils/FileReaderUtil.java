package org.example.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileReaderUtil {

    /**
     * Reads the content of a file and returns it as a single string.
     *
     * @param fileName the name of the file to read
     * @return the content of the file as a single string
     * @throws IOException if an error occurs while reading the file
     */
    public static String readFileAsString(String fileName) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append(System.lineSeparator()); // Append each line
            }
        }

        return contentBuilder.toString().trim(); // Trim to remove the last newline character
    }
}
