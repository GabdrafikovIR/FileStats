package com.rostelecomtest.filestats.output;

import com.rostelecomtest.filestats.core.ExtensionStats;

import java.util.Map;

public interface OutputFormatter {

    /**
     * @param statsMap Map: расширение -> статистика
     * @return строка в нужном формате
     */


    String format(Map<String, ExtensionStats> statsMap);
}
