package com.rostelecomtest.filestats.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ParallelStatsAggregator — многопоточная агрегация статистики")
class ParallelStatsAggregatorTest {

    @Test
    @DisplayName("Многопоточный результат совпадает с однопоточным")
    void testParallelAggregationEqualsSequential() throws IOException {
        // given: временные файлы
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

        // when: запускаем агрегацию в 1 и в несколько потоков
        StatsAggregator aggregator = new StatsAggregator();
        Map<String, ExtensionStats> sequentialResult = aggregator.aggregate(
                List.of(javaFile, shFile),
                processor
        );

        ParallelStatsAggregator parallelAggregator = new ParallelStatsAggregator();
        Map<String, ExtensionStats> parallelResult = parallelAggregator.aggregateParallel(
                List.of(javaFile, shFile),
                processor,
                4
        );

        // then: результаты должны совпадать
        assertAll(
                () -> assertEquals(sequentialResult.keySet(), parallelResult.keySet(),
                        "Набор расширений должен совпадать"),
                () -> assertEquals(sequentialResult.get("java").getTotalLines(),
                        parallelResult.get("java").getTotalLines(),
                        "Количество строк для .java должно совпадать"),
                () -> assertEquals(sequentialResult.get("sh").getTotalLines(),
                        parallelResult.get("sh").getTotalLines(),
                        "Количество строк для .sh должно совпадать")
        );

        Files.deleteIfExists(javaFile);
        Files.deleteIfExists(shFile);
    }

    @Test
    @DisplayName("Ошибки при обработке файлов обрабатываются корректно (без падений)")
    void testParallelHandlesErrorsGracefully() {
        FileProcessor processor = new FileProcessor();
        ParallelStatsAggregator parallelAggregator = new ParallelStatsAggregator();

        // given: несуществующий файл
        Path badFile = Path.of("does_not_exist.java");

        // when
        Map<String, ExtensionStats> result = parallelAggregator.aggregateParallel(
                List.of(badFile),
                processor,
                2
        );

        // then
        assertTrue(result.isEmpty(), "Результат должен быть пустым при ошибке чтения файла");
    }
}
