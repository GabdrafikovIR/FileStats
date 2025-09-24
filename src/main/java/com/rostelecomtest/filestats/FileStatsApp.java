package com.rostelecomtest.filestats;

import com.rostelecomtest.filestats.cli.Arguments;
import com.rostelecomtest.filestats.core.*;
import com.rostelecomtest.filestats.output.*;
import com.rostelecomtest.filestats.output.outputImpl.JsonOutputFormatter;
import com.rostelecomtest.filestats.output.outputImpl.PlainOutputFormatter;
import com.rostelecomtest.filestats.output.outputImpl.XmlOutputFormatter;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileStatsApp implements Callable<Integer> {

    @CommandLine.Mixin
    private Arguments arguments;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new FileStatsApp()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            // Сбор файлов для обработки
            List<Path> files = collectFiles();

            // Подсчёт статистики
            FileProcessor processor = new FileProcessor();
            StatsAggregator aggregator = new StatsAggregator();
            Map<String, ExtensionStats> statsMap = aggregator.aggregate(files, processor);

            // Форматирование
            OutputFormatter formatter = getFormatter(arguments.output);
            String result = formatter.format(statsMap);

            // Вывод в консоль
            System.out.println(result);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            return 1;
        }
        return 0;
    }

    /**
     * Сбор файлов по параметрам CLI.
     */
    private List<Path> collectFiles() throws IOException {
        Path startPath = Paths.get(arguments.path);

        int maxDepth = arguments.maxDepth != null ? arguments.maxDepth : Integer.MAX_VALUE;

        // Загружаем .gitignore, если надо
        List<String> ignorePatterns = arguments.gitIgnore ? loadGitIgnore(startPath) : List.of();

        try (Stream<Path> stream = arguments.recursive
                ? Files.walk(startPath, maxDepth)
                : Files.list(startPath)) {

            return stream
                    .filter(Files::isRegularFile)
                    .filter(this::filterByIncludeExclude)
                    .filter(f -> !isIgnored(f, ignorePatterns, startPath))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Фильтрация файлов по расширениям.
     */
    private boolean filterByIncludeExclude(Path file) {
        String ext = getExtension(file);

        if (!arguments.includeExt.isEmpty() && !arguments.includeExt.contains(ext)) {
            return false;
        }
        if (arguments.excludeExt.contains(ext)) {
            return false;
        }
        return true;
    }

    private String getExtension(Path file) {
        String name = file.getFileName().toString();
        int lastDot = name.lastIndexOf(".");
        return lastDot == -1 ? "unknown" : name.substring(lastDot + 1);
    }

    /**
     * Загружает строки из .gitignore
     */
    private List<String> loadGitIgnore(Path baseDir) throws IOException {
        Path gitignore = baseDir.resolve(".gitignore");
        if (!Files.exists(gitignore)) {
            return List.of();
        }
        return Files.readAllLines(gitignore).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .collect(Collectors.toList());
    }

    /**
     * Проверка, нужно ли исключить файл по .gitignore
     */
    private boolean isIgnored(Path file, List<String> patterns, Path baseDir) {
        Path relative = baseDir.relativize(file);
        String relPath = relative.toString().replace("\\", "/");
        return patterns.stream().anyMatch(relPath::contains);
    }

    /**
     * Определяет формат по CLI-параметру.
     */
    private OutputFormatter getFormatter(String type) {
        switch (type.toLowerCase()) {
            case "json":
                return new JsonOutputFormatter();
            case "xml":
                return new XmlOutputFormatter();
            default:
                return new PlainOutputFormatter();
        }
    }
}
