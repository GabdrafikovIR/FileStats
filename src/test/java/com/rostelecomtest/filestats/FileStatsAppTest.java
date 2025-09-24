package com.rostelecomtest.filestats;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileStatsApp — интеграционные тесты CLI")
class FileStatsAppTest {

    @Test
    @DisplayName("Формат plain: статистика выводится в читаемом виде")
    void testPlainOutput(@TempDir Path tempDir) throws Exception {
        Path javaFile = tempDir.resolve("Test.java");
        Files.write(javaFile, """
                // comment
                public class Test {}
                """.getBytes());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            int exitCode = new CommandLine(new FileStatsApp()).execute(
                    tempDir.toString(),
                    "--include-ext=java",
                    "--output=plain"
            );

            String output = out.toString();
            assertAll(
                    () -> assertEquals(0, exitCode),
                    () -> assertTrue(output.contains("Расширение: java")),
                    () -> assertTrue(output.contains("Файлов")),
                    () -> assertTrue(output.contains("Строк"))
            );
        } finally {
            System.setOut(oldOut);
        }
    }

    @Test
    @DisplayName("Формат json: статистика выводится в JSON")
    void testJsonOutput(@TempDir Path tempDir) throws Exception {
        Path shFile = tempDir.resolve("script.sh");
        Files.write(shFile, """
                #!/bin/bash
                # comment
                echo hi
                """.getBytes());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            int exitCode = new CommandLine(new FileStatsApp()).execute(
                    tempDir.toString(),
                    "--include-ext=sh",
                    "--output=json"
            );

            String output = out.toString();
            assertAll(
                    () -> assertEquals(0, exitCode),
                    () -> assertTrue(output.contains("\"sh\"")),
                    () -> assertTrue(output.contains("\"fileCount\"")),
                    () -> assertTrue(output.contains("\"totalLines\""))
            );
        } finally {
            System.setOut(oldOut);
        }
    }

    @Test
    @DisplayName("Формат xml: статистика выводится в XML")
    void testXmlOutput(@TempDir Path tempDir) throws Exception {
        Path txtFile = tempDir.resolve("notes.txt");
        Files.write(txtFile, """
                line1
                line2
                """.getBytes());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            int exitCode = new CommandLine(new FileStatsApp()).execute(
                    tempDir.toString(),
                    "--include-ext=txt",
                    "--output=xml"
            );

            String output = out.toString();
            assertAll(
                    () -> assertEquals(0, exitCode),
                    () -> assertTrue(output.contains("<extension name=\"txt\">")),
                    () -> assertTrue(output.contains("<fileCount>1</fileCount>")),
                    () -> assertTrue(output.contains("<totalLines>2</totalLines>"))
            );
        } finally {
            System.setOut(oldOut);
        }
    }

    @Test
    @DisplayName("Рекурсивный обход (--recursive) находит файлы во вложенных каталогах")
    void testRecursiveOption(@TempDir Path tempDir) throws Exception {
        // given: создаём структуру папок
        Path subDir = tempDir.resolve("subdir");
        Files.createDirectories(subDir);

        Path javaFile = subDir.resolve("Inner.java");
        Files.write(javaFile, """
                // comment
                class Inner {}
                """.getBytes());

        // перехват консоли
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            // when: запускаем с рекурсией
            int exitCode = new CommandLine(new FileStatsApp()).execute(
                    tempDir.toString(),
                    "--recursive",
                    "--max-depth=5",
                    "--include-ext=java",
                    "--output=plain"
            );

            // then
            String output = out.toString();
            assertAll(
                    () -> assertEquals(0, exitCode),
                    () -> assertTrue(output.contains("java"), "Вывод должен содержать статистику по java"),
                    () -> assertTrue(output.contains("Файлов: 1"), "Должен быть найден один файл " +
                            "во вложенной папке")
            );
        } finally {
            System.setOut(oldOut);
        }
    }

    @Test
    @DisplayName("Файлы с расширениями из --exclude-ext игнорируются")
    void testExcludeOption(@TempDir Path tempDir) throws Exception {
        // given: создаём два файла
        Path javaFile = tempDir.resolve("Test.java");
        Files.write(javaFile, """
                class Test {}
                """.getBytes());

        Path txtFile = tempDir.resolve("notes.txt");
        Files.write(txtFile, """
                line1
                line2
                """.getBytes());

        // перехват System.out
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            // when: исключаем .txt файлы
            int exitCode = new CommandLine(new FileStatsApp()).execute(
                    tempDir.toString(),
                    "--recursive",
                    "--exclude-ext=txt",
                    "--output=plain"
            );

            // then
            String output = out.toString();
            assertAll(
                    () -> assertEquals(0, exitCode),
                    () -> assertTrue(output.contains("java"), "Java файл должен попасть в отчёт"),
                    () -> assertFalse(output.contains("txt"), "TXT файл не должен попасть в отчёт")
            );
        } finally {
            System.setOut(oldOut);
        }
    }

    @Test
    @DisplayName("Файлы фильтруются по --include-ext")
    void testIncludeOption(@TempDir Path tempDir) throws Exception {
        // given: создаём java и txt файлы
        Path javaFile = tempDir.resolve("Test.java");
        Files.write(javaFile, "class Test {}".getBytes());

        Path txtFile = tempDir.resolve("notes.txt");
        Files.write(txtFile, "line1\nline2".getBytes());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            // when: оставляем только java
            int exitCode = new CommandLine(new FileStatsApp()).execute(
                    tempDir.toString(),
                    "--include-ext=java",
                    "--output=plain"
            );

            String output = out.toString();
            assertAll(
                    () -> assertEquals(0, exitCode),
                    () -> assertTrue(output.contains("java"), "Java должен попасть в отчёт"),
                    () -> assertFalse(output.contains("txt"), "TXT не должен попасть в отчёт")
            );
        } finally {
            System.setOut(oldOut);
        }
    }

    @Test
    @DisplayName("Файл без расширения попадает в категорию unknown")
    void testFileWithoutExtension(@TempDir Path tempDir) throws Exception {
        // given: файл без расширения
        Path noExtFile = tempDir.resolve("README");
        Files.write(noExtFile, "hello\nworld".getBytes());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            // when
            int exitCode = new CommandLine(new FileStatsApp()).execute(
                    tempDir.toString(),
                    "--output=plain"
            );

            String output = out.toString();
            assertAll(
                    () -> assertEquals(0, exitCode),
                    () -> assertTrue(output.contains("unknown"), "Файл без расширения должен " +
                            "попасть в категорию unknown")
            );
        } finally {
            System.setOut(oldOut);
        }
    }

}
