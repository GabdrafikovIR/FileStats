package com.rostelecomtest.filestats.output;

import com.rostelecomtest.filestats.core.ExtensionStats;
import com.rostelecomtest.filestats.core.Stats;
import com.rostelecomtest.filestats.output.outputImpl.JsonOutputFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonOutputFormatter — тестирование JSON-вывода")
class JsonOutputFormatterTest {

    @Test
    @DisplayName("Форматирование статистики в JSON")
    void testFormatJson() {
        // given
        ExtensionStats shStats = new ExtensionStats("sh");
        shStats.addFileStats(null, new Stats(
                "script.sh", "sh", 5, 3, 1
        ));

        OutputFormatter formatter = new JsonOutputFormatter();

        // when
        String result = formatter.format(Map.of("sh", shStats));

        // then
        assertAll(
                () -> assertTrue(result.contains("\"sh\""), "JSON должен содержать ключ 'sh'"),
                () -> assertTrue(result.contains("\"fileCount\""), "JSON должен содержать поле fileCount"),
                () -> assertTrue(result.contains("5"), "JSON должен содержать значение строк")
        );
    }
}
