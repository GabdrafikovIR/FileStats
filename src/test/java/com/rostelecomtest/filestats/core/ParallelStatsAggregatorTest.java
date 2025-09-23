package com.rostelecomtest.filestats.core;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParallelStatsAggregatorTest {

    @Test
    void testParallelAggregationEqualsSequential() throws IOException {
        // Создаем временные файлы для теста
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

        // Однопоточный результат
        StatsAggregator aggregator = new StatsAggregator();
        Map<String, ExtensionStats> sequentialResult = aggregator.aggregate(
                List.of(javaFile, shFile),
                processor
        );

        // Многопоточный результат
        ParallelStatsAggregator parallelAggregator = new ParallelStatsAggregator();
        Map<String, ExtensionStats> parallelResult = parallelAggregator.aggregateParallel(
                List.of(javaFile, shFile),
                processor,
                4
        );

        // Сравниваем
        assertEquals(sequentialResult.keySet(), parallelResult.keySet());
        assertEquals(sequentialResult.get("java").getTotalLines(),
                parallelResult.get("java").getTotalLines());
        assertEquals(sequentialResult.get("sh").getTotalLines(),
                parallelResult.get("sh").getTotalLines());

        Files.deleteIfExists(javaFile);
        Files.deleteIfExists(shFile);
    }

    @Test
    void testParallelHandlesErrorsGracefully() {
        FileProcessor processor = new FileProcessor();
        ParallelStatsAggregator parallelAggregator = new ParallelStatsAggregator();

        // Передаём несуществующий файл
        Path badFile = Path.of("does_not_exist.java");

        Map<String, ExtensionStats> result = parallelAggregator.aggregateParallel(
                List.of(badFile),
                processor,
                2
        );

        // Результат должен быть пустым, без падений
        assertTrue(result.isEmpty());
    }
}
