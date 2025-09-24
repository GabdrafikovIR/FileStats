package com.rostelecomtest.filestats.core;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregator — собирает статистику по списку файлов.
 * Группировка идёт по расширению.
 */
public class StatsAggregator {

    /**
     * Обходит список файлов, считает статистику
     * и группирует её по расширению.
     *
     * @param files     список файлов для анализа
     * @param processor процессор для подсчёта статистики по одному файлу
     * @return Map: ключ = расширение, значение = агрегированная статистика
     */
    public Map<String, ExtensionStats> aggregate(List<Path> files, FileProcessor processor) {
        Map<String, ExtensionStats> result = new HashMap<>();

        for (Path file : files) {
            try {
                Stats fileStats = processor.processFile(file);

                result.computeIfAbsent(fileStats.getExtension(), ExtensionStats::new)
                        .addFileStats(file, fileStats);

            } catch (IOException e) {
                System.err.println("Не удалось обработать файл: " + file + " — " + e.getMessage());
            }
        }

        return result;
    }
}
