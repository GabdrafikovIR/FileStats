package com.rostelecomtest.filestats.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StatsAggregator — агрегирование статистики по расширениям")
class StatsAggregatorTest {

    @Test
    @DisplayName("Агрегация статистики для нескольких файлов (.java и .sh)")
    void testAggregateMultipleFiles() throws IOException {
        // given
        Path javaFile = Files.createTempFile("A", ".java");
        Files.write(javaFile, """
                // comment
                class A {}
                """.getBytes());

        Path shFile = Files.createTempFile("B", ".sh");
        Files.write(shFile, """
                #!/bin/bash
                # comment
                echo "hi"
                """.getBytes());

        FileProcessor processor = new FileProcessor();
        StatsAggregator aggregator = new StatsAggregator();

        // when
        Map<String, ExtensionStats> result = aggregator.aggregate(
                List.of(javaFile, shFile),
                processor
        );

        // then
        assertAll(
                () -> assertTrue(result.containsKey("java"), "Должен присутствовать ключ 'java'"),
                () -> assertTrue(result.containsKey("sh"), "Должен присутствовать ключ 'sh'")
        );

        ExtensionStats javaStats = result.get("java");
        assertAll(
                () -> assertEquals(1, javaStats.getFileCount(), "Неверное количество файлов для java"),
                () -> assertEquals(2, javaStats.getTotalLines(), "Неверное количество строк для java"),
                () -> assertEquals(1, javaStats.getCommentLines(), "Неверное количество комментариев для java")
        );

        ExtensionStats shStats = result.get("sh");
        assertAll(
                () -> assertEquals(1, shStats.getFileCount(), "Неверное количество файлов для sh"),
                () -> assertEquals(3, shStats.getTotalLines(), "Неверное количество строк для sh"),
                () -> assertEquals(1, shStats.getCommentLines(), "Неверное количество комментариев для sh")
        );

        Files.deleteIfExists(javaFile);
        Files.deleteIfExists(shFile);
    }
}
