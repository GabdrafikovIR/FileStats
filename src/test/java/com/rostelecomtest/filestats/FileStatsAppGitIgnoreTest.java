package com.rostelecomtest.filestats;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FileStatsAppGitIgnoreTest {

    @Test
    @DisplayName("Файлы из .gitignore не должны обрабатываться")
    void testGitIgnore(@TempDir Path tempDir) throws Exception {
        // .gitignore
        Path gitignore = tempDir.resolve(".gitignore");
        Files.writeString(gitignore, "ignoreme.txt");

        // Игнорируемый файл
        Path ignoredFile = tempDir.resolve("ignoreme.txt");
        Files.writeString(ignoredFile, "ignored content");

        // Файл, который должен быть обработан
        Path validFile = tempDir.resolve("valid.java");
        Files.writeString(validFile, """
                // comment
                class Valid {}
                """);

        // Перехват System.out
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            FileStatsApp app = new FileStatsApp();
            new picocli.CommandLine(app).execute(
                    tempDir.toString(),
                    "--git-ignore",
                    "--output=plain"
            );

            String output = out.toString();
            assertTrue(output.contains("java"), "Должна быть статистика по valid.java");
            assertTrue(!output.contains("ignoreme.txt"), "ignoreme.txt не должен попасть в статистику");
        } finally {
            System.setOut(oldOut);
        }
    }

    @Test
    @DisplayName("Если .gitignore пустой — все файлы обрабатываются")
    void testEmptyGitIgnore(@TempDir Path tempDir) throws Exception {
        // Пустой .gitignore
        Path gitignore = tempDir.resolve(".gitignore");
        Files.writeString(gitignore, "");

        // Два файла
        Path file1 = tempDir.resolve("one.txt");
        Files.writeString(file1, "line1");

        Path file2 = tempDir.resolve("two.java");
        Files.writeString(file2, "class Two {}");

        // Перехват System.out
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            FileStatsApp app = new FileStatsApp();
            new picocli.CommandLine(app).execute(
                    tempDir.toString(),
                    "--git-ignore",
                    "--output=plain"
            );

            String output = out.toString();
            assertTrue(output.contains("txt"), "Должна быть статистика по one.txt");
            assertTrue(output.contains("java"), "Должна быть статистика по two.java");
        } finally {
            System.setOut(oldOut);
        }
    }
}
