package com.rostelecomtest.filestats.core;


import lombok.Getter;

@Getter
public class ExtensionStats {
    private final String extension;  // расширение, например "java" или "sh"
    private int fileCount;           // количество файлов
    private long totalSizeBytes;     // общий размер файлов (в байтах)
    private int totalLines;          // общее количество строк
    private int nonEmptyLines;       // количество непустых строк
    private int commentLines;        // количество строк с комментариями

    public ExtensionStats(String extension) {
        this.extension = extension;
    }

    public void addFileStats(java.nio.file.Path file, Stats stats) {
        fileCount++;
        try {
            totalSizeBytes += java.nio.file.Files.size(file);
        } catch (Exception ignored) {
            // если не удалось получить размер файла — пропускаем
        }
        totalLines += stats.getTotalLines();
        nonEmptyLines += stats.getNonEmptyLines();
        commentLines += stats.getCommentLines();
    }

    @Override
    public String toString() {
        return String.format(
                "Расширение: %s | Файлов: %d | Размер: %d байт | Строк: %d | Непустых: %d | Комментариев: %d",
                extension, fileCount, totalSizeBytes, totalLines, nonEmptyLines, commentLines
        );
    }
}
