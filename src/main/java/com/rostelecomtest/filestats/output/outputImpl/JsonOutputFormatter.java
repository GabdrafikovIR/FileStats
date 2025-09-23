package com.rostelecomtest.filestats.output;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rostelecomtest.filestats.core.ExtensionStats;

import java.util.Map;

/**
 * Форматирует статистику в JSON.
 */

public class JsonOutputFormatter implements OutputFormatter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String format(Map<String, ExtensionStats> statsMap) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(statsMap);
        } catch (JsonProcessingException e) {
            return "Ошибка сериализации в JSON: " + e.getMessage();
        }
    }
}
