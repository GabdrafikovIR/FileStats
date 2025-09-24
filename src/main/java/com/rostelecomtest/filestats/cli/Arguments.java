package com.rostelecomtest.filestats.cli;

import picocli.CommandLine;

import java.util.*;

/**
 * CLI-параметры для утилиты FileStats.
 */
public class Arguments {

    @CommandLine.Parameters(index = "0", description = "Путь до каталога для анализа")
    public String path;

    @CommandLine.Option(names = "--recursive", description = "Рекурсивный обход подкаталогов")
    public boolean recursive = false;

    @CommandLine.Option(names = "--max-depth", description = "Максимальная глубина обхода")
    public Integer maxDepth;

    @CommandLine.Option(names = "--thread", description = "Количество потоков для обработки")
    public int threads = 1;

    @CommandLine.Option(names = "--include-ext", split = ",", description = "Обрабатывать только указанные" +
            " расширения файлов (через запятую)")
    public Set<String> includeExt = new HashSet<>();

    @CommandLine.Option(names = "--exclude-ext", split = ",", description = "Исключить файлы с этими расширениями" +
            " (через запятую)")
    public Set<String> excludeExt = new HashSet<>();

    @CommandLine.Option(names = "--git-ignore", description = "Игнорировать файлы из .gitignore")
    public boolean gitIgnore = false;

    @CommandLine.Option(names = "--output", description = "Формат вывода: plain, json, xml", defaultValue = "plain")
    public String output = "plain";
}
