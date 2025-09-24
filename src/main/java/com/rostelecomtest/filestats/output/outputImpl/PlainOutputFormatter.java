package com.rostelecomtest.filestats.output.outputImpl;

import com.rostelecomtest.filestats.core.ExtensionStats;
import com.rostelecomtest.filestats.output.OutputFormatter;

import java.util.Map;
import java.util.stream.Collectors;

public class PlainOutputFormatter implements OutputFormatter {

    @Override
    public String format(Map<String, ExtensionStats> statsMap) {
        return statsMap.values().stream()
                .map(ExtensionStats::toString)
                .collect(Collectors.joining("\n"));
    }


}
