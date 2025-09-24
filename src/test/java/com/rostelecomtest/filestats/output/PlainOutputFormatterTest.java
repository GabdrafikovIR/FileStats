package com.rostelecomtest.filestats.output;

import com.rostelecomtest.filestats.core.ExtensionStats;
import com.rostelecomtest.filestats.core.Stats;
import com.rostelecomtest.filestats.output.outputImpl.PlainOutputFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PlainOutputFormatter — тестирование текстового вывода")
class PlainOutputFormatterTest {

    @Test
    @DisplayName("Форматирование статистики в текст")
    void testFormatPlain() {
        // given
        ExtensionStats javaStats = new ExtensionStats("java");
        javaStats.addFileStats(null, new Stats(
                "Test.java", "java", 10, 8, 2
        ));

        OutputFormatter formatter = new PlainOutputFormatter();

        // when
        String result = formatter.format(Map.of("java", javaStats));

        // then
        assertAll(
                () -> assertTrue(result.contains("Расширение: java"), "Должно быть указано расширение"),
                () -> assertTrue(result.contains("Файлов: 1"), "Должно отображаться количество файлов"),
                () -> assertTrue(result.contains("Строк: 10"), "Должно отображаться количество строк")
        );
    }
}
