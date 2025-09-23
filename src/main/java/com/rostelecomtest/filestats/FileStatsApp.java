package com.rostelecomtest;

import com.rostelecomtest.cli.Arguments;
import picocli.CommandLine;

public class FileStatsApp
{
    public static void main( String[] args ) {
        int exitCode = new CommandLine(new Arguments()).execute(args);
        System.exit(exitCode);
    }
}
