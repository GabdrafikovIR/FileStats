package com.rostelecomtest.filestats.output.outputImpl;

import com.rostelecomtest.filestats.core.ExtensionStats;
import com.rostelecomtest.filestats.output.OutputFormatter;

import java.util.Map;


/**
 * Форматирует статистику в XML (примитивная реализация).
 */
public class XmlOutputFormatter implements OutputFormatter {


    @Override
    public String format(Map<String, ExtensionStats> statsMap) {

        StringBuilder sb = new StringBuilder();
        sb.append("<stats>\n>");

        for (ExtensionStats stats : statsMap.values()) {
            sb.append("  <extension name=\"").append(stats.getExtension()).append("\">\n")
                    .append("    <fileCount>").append(stats.getFileCount()).append("</fileCount>\n")
                    .append("    <totalSizeBytes>").append(stats.getTotalSizeBytes()).append("</totalSizeBytes>\n")
                    .append("    <totalLines>").append(stats.getTotalLines()).append("</totalLines>\n")
                    .append("    <nonEmptyLines>").append(stats.getNonEmptyLines()).append("</nonEmptyLines>\n")
                    .append("    <commentLines>").append(stats.getCommentLines()).append("</commentLines>\n")
                    .append("  </extension>\n");
        }

        sb.append("</stats>");
        return sb.toString();
    }
}
