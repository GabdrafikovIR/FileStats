package com.rostelecomtest.filestats.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileProcessor — подсчёт статистики по файлам")
class FileProcessorTest {

    @Test
    @DisplayName("Подсчёт строк и комментариев в Java-файле")
    void testProcessFileCountsLinesCorrectly() throws IOException {
        // given: временный java-файл
        Path tempFile = Files.createTempFile("test", ".java");
        Files.write(tempFile, """
                // comment
                public class Test {
                  // another comment
                  String s = "hello";
                }
                """.getBytes());

        FileProcessor processor = new FileProcessor();

        // when
        Stats stats = processor.processFile(tempFile);

        // then
        assertAll(
                () -> assertEquals(5, stats.getTotalLines(), "Общее количество строк должно быть 5"),
                () -> assertEquals(5, stats.getNonEmptyLines(), "Количество непустых строк должно быть 5"),
                () -> assertEquals(2, stats.getCommentLines(), "Количество строк с комментариями должно быть 2")
        );

        Files.deleteIfExists(tempFile);
    }

    @Test
    @DisplayName("Обработка пустого файла")
    void testEmptyFile() throws IOException {
        // given: пустой файл
        Path tempFile = Files.createTempFile("empty", ".txt");

        FileProcessor processor = new FileProcessor();

        // when
        Stats stats = processor.processFile(tempFile);

        // then
        assertAll(
                () -> assertEquals(0, stats.getTotalLines(), "Общее количество строк должно быть 0"),
                () -> assertEquals(0, stats.getNonEmptyLines(), "Количество непустых строк должно быть 0"),
                () -> assertEquals(0, stats.getCommentLines(), "Количество строк с комментариями должно быть 0")
        );

        Files.deleteIfExists(tempFile);
    }
}
