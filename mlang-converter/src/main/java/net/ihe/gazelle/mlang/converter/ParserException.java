package net.ihe.gazelle.mlang.converter;

import java.util.List;

public class ParserException extends Exception {
    public ParserException(List<SyntaxError> errors) {
        super(buildMessage(errors));
    }

    private static String buildMessage(List<SyntaxError> errors) {
        StringBuilder builder = new StringBuilder();
        for (var error : errors) {
            builder.append("\n\t").append(error.toString());
        }
        return builder.toString();
    }
}
