package net.ihe.gazelle.mlang.converter;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public interface Converter {
    void convert(InputStreamReader is, OutputStreamWriter os) throws ConverterException, ParserException;
}
