package nemethi.jsonprettyprinter;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import static java.util.Objects.requireNonNull;

public class JsonPrettyPrinter {

    private static final int DEFAULT_INDENT_AMOUNT = 2;
    private static final String EMPTY_STRING = "";
    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String ARRAY_START = "[";
    private static final String ARRAY_END = "]";
    private static final String OBJECT_START = "{";
    private static final String OBJECT_END = "}";

    private final int indentAmount;
    private PrintWriter printer;

    public JsonPrettyPrinter() {
        this(DEFAULT_INDENT_AMOUNT);
    }

    public JsonPrettyPrinter(int indentAmount) {
        validate(indentAmount);
        this.indentAmount = indentAmount;
    }

    private void validate(int indentAmount) {
        if (indentAmount < 0 || indentAmount > 10) {
            throw new IllegalArgumentException("indentAmount must be between 0 and 10 inclusive");
        }
    }

    public int getIndentAmount() {
        return indentAmount;
    }

    public void prettyPrint(String json, Writer writer) {
        requireNonNull(writer, "writer cannot be null");
        printer = new PrintWriter(writer, true);
        prettyPrint(json);
    }

    public void prettyPrint(String json, OutputStream outputStream) {
        requireNonNull(outputStream, "outputStream cannot be null");
        printer = new PrintWriter(outputStream, true);
        prettyPrint(json);
    }

    private void prettyPrint(String json) {
        JsonTokenizer tokenizer = new JsonTokenizer(json);

        int level = 0;
        String previousToken = EMPTY_STRING;
        for (String token : tokenizer) {
            level = prettyPrint(token, previousToken, level);
            previousToken = token;
        }
        printer.flush();
        printer = null;
    }

    private int prettyPrint(String token, String previousToken, int level) {
        if (isStructureStart(token)) {
            level = prettyPrintStructureStart(token, previousToken, level);
        } else if (isStructureEnd(token)) {
            level = prettyPrintStructureEnd(token, previousToken, level);
        } else {
            prettyPrintToken(level, previousToken, token);
        }
        return level;
    }

    private int prettyPrintStructureStart(String token, String previousToken, int level) {
        if (previousToken.equals(COMMA)) {
            println();
        }
        if (isStructureStart(previousToken)) {
            lnprint(token, level++);
        } else {
            if (previousToken.equals(COLON)) {
                print(SPACE);
                print(token);
                level++;
            } else {
                print(token, level++);
            }
        }
        return level;
    }

    private int prettyPrintStructureEnd(String token, String previousToken, int level) {
        if (isStructureStart(previousToken)) {
            print(token);
            --level;
        } else {
            lnprint(token, --level);
        }
        return level;
    }

    private boolean isStructureStart(String token) {
        return token.equals(ARRAY_START) || token.equals(OBJECT_START);
    }

    private boolean isStructureEnd(String token) {
        return token.equals(ARRAY_END) || token.equals(OBJECT_END);
    }

    private void prettyPrintToken(int level, String prevToken, String token) {
        if (token.equals(COMMA) || token.equals(COLON)) {
            print(token);
        } else {
            if (prevToken.equals(COLON)) {
                print(SPACE);
                print(token);
            } else {
                lnprint(token, level);
            }
        }
    }

    private void println() {
        printer.println();
    }

    private void print(String string) {
        printer.print(string);
    }

    private void print(String string, int level) {
        printer.print(indent(string, level));
    }

    private void lnprint(String string, int level) {
        printer.println();
        printer.print(indent(string, level));
    }

    private String indent(String string, int level) {
        return SPACE.repeat(level * indentAmount) + string;
    }
}
