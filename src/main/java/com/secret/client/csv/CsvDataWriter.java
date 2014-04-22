package com.secret.client.csv;

import static au.com.bytecode.opencsv.CSVWriter.NO_QUOTE_CHARACTER;
import java.io.FileWriter;
import java.io.IOException;
import au.com.bytecode.opencsv.CSVWriter;

public abstract class CsvDataWriter {

    private static final char CSV_SEPARATOR = ';';

    protected final CSVWriter writer;

    public CsvDataWriter(String outputFile) throws IOException {
        final FileWriter fileWriter = new FileWriter(outputFile);
        this.writer = new CSVWriter(fileWriter, CSV_SEPARATOR, NO_QUOTE_CHARACTER);
    }


}
