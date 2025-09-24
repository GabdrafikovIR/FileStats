package com.rostelecomtest.filestats.output;

import com.rostelecomtest.filestats.core.ExtensionStats;
import com.rostelecomtest.filestats.core.Stats;
import com.rostelecomtest.filestats.output.outputImpl.XmlOutputFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование XML-формата")
class XmlOutputFormatterTest {

    @Test
    @DisplayName("Корректное преобразование статистики в XML")
    void testFormatXml() {
        // given
        ExtensionStats txtStats = new ExtensionStats("txt");
        txtStats.addFileStats(null, new Stats(
                "notes.txt", "txt", 7, 6, 0
        ));

        OutputFormatter formatter = new XmlOutputFormatter();

        // when
        String result = formatter.format(Map.of("txt", txtStats));

        // then
        assertAll(
                () -> assertTrue(result.contains("<extension name=\"txt\">"), "XML должен содержать тег расширения"),
                () -> assertTrue(result.contains("<fileCount>1</fileCount>"), "XML должен содержать количество файлов"),
                () -> assertTrue(result.contains("<totalLines>7</totalLines>"), "XML должен содержать общее количество строк")
        );
    }
}
