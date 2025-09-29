package com.rostelecomtest.filestats.core;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

/**
 * Параллельный агрегатор — обрабатывает файлы в несколько потоков.
 */
public class ParallelStatsAggregator extends StatsAggregator {

    /**
     * Обходит список файлов в несколько потоков.
     *
     * @param files список файлов
     * @param processor FileProcessor
     * @param threads количество потоков
     * @return Map: расширение -> статистика
     */
    public Map<String, ExtensionStats> aggregateParallel(List<Path> files, FileProcessor processor, int threads) {
        Map<String, ExtensionStats> result = new ConcurrentHashMap<>();

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Future<Stats>> futures = new ArrayList<>();

        // Отправляем задачи в пул
        for (Path file : files) {
            futures.add(executor.submit(() -> {
                try {
                    return processor.processFile(file);
                } catch (IOException e) {
                    System.err.println("Ошибка чтения файла: " + file + " — " + e.getMessage());
                    return null;
                }
            }));
        }

        // Собираем результаты
        for (Future<Stats> future : futures) {
            try {
                Stats stats = future.get(); // блокируемся пока задача не завершится
                if (stats != null) {
                    result.computeIfAbsent(stats.getExtension(), ExtensionStats::new)
                          .addFileStats(Path.of(stats.getFilePath()), stats);
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Ошибка выполнения задачи: " + e.getMessage());
            }
        }

        executor.shutdown();
        return result;
    }
}
