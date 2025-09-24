package com.rostelecomtest.filestats.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileProcessor {

    /**
     * @param file путь к файлу
     * @return объект Stats с результатами
     * @throws IOException если файл не удалось прочитать
     */
    public Stats processFile(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);

        int totalLines = lines.size();
        int nonEmptyLines = 0;
        int commentLines = 0;

        for (String line : lines) {
            String trimmedLine = line.trim();

            if (!trimmedLine.isEmpty()) {
                nonEmptyLines++;
            }

            // учитываем комментарии
            if (isComment(trimmedLine)) {
                commentLines++;
            }
        }

        return new Stats(
                file.toString(),
                getExtension(file),
                totalLines,
                nonEmptyLines,
                commentLines
        );
    }

    /**
     * Определяет, является ли строка комментарием.
     * Для Java — строки, начинающиеся с "//".
     * Для Bash — строки, начинающиеся с "#", кроме шейбанга (#!).
     */
    private boolean isComment(String line) {
        if (line.startsWith("//")) {
            return true;
        }
        if (line.startsWith("#") && !line.startsWith("#!")) {
            return true;
        }
        return false;
    }

    /**
     * @param file путь к файлу
     * @return расширение (без точки), либо "unknown"
     */
    private String getExtension(Path file) {
        String fileName = file.getFileName().toString();
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot == -1) {
            return "unknown";
        }
        return fileName.substring(lastDot + 1);
    }
}
