package com.rostelecomtest.filestats.core;

import lombok.Value;

@Value
public class Stats {
    String filePath;     // полный путь к файлу
    String extension;    // тип файла (расширение)
    int totalLines;      // общее количество строк
    int nonEmptyLines;   // строки с содержимым (не только пробелы)
    int commentLines;    // строки-комментарии (// или # в начале)
}
