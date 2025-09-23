package com.rostelecomtest.cli;

import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;


@Command(
        name = "filestats",
        mixinStandardHelpOptions = true,
        version = "FileStats 1.0",
        description = "Собирает статистику по файлам в директории"
)
public class Arguments implements Runnable {

    @Parameters(index = "0", description = "Путь к каталогу для выполнения анализа")
    private String path;

    @Option(names = "--recursive", description = "Рекурсивный обход подкаталогов")
    private boolean recursive;

    @Option(names = "--max-depth", description = "Максимальная глубина обхода (по умолчанию без ограничений)")
    private int maxDepth;

    @Option(names = "--threads", description = "Количество потоков для анализа (по умолчанию 1)")
    private int threads = 1;

    @Option(names = "--include-ext", split = ",", description = "Обрабатывать только указанные расширения файлов")
    private String[] includeExt;

    @Option(names = "--exclude-ext", split = ",", description = "Исключить указанные расширения файлов")
    private String[] excludeExt;

    @Option(names = "--output", description = "Формат вывода: plain, json, xml (по умолчанию plain)")
    private String output = "plain";

    @Override
    public void run() {
        System.out.println("Директория = " + path);
        System.out.println("Рекурсивный обход = " + recursive);
        System.out.println("Максимальная глубина рекурсивного обхода = " + maxDepth);
        System.out.println("Количество потоков для анализа = " + threads);
        System.out.println("Обрабатываемые расширения файлов = " + (includeExt == null ? "null" :
                String.join(",", includeExt)));
        System.out.println("Исключить расширения файлов = " + (excludeExt == null ? "null" :
                String.join(",", excludeExt)));
        System.out.println("Формат вывода (plain, json, xml) = " + output);
    }

    public String getPath() {
        return path;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getThreads() {
        return threads;
    }

    public String[] getIncludeExt() {
        return includeExt;
    }

    public String[] getExcludeExt() {
        return excludeExt;
    }

    public String getOutput() {
        return output;
    }
}
