package net.ihe.gazelle.mlang.converter;

public class SyntaxError {
    private int line;
    private int column;
    private String message;

    public SyntaxError(int line, int column, String message) {
        this.line = line;
        this.column = column;
        this.message = message;
    }

    public int getLine() { return this.line; }
    public int getColumn() { return this.column; }
    public String getMessage() { return this.message; }

    @Override
    public String toString() {
        return "SyntaxError{" +
                "line=" + line +
                ", column=" + column +
                ", message='" + message + '\'' +
                '}';
    }
}
