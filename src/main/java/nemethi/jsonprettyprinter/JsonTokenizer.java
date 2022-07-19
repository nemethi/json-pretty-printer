package nemethi.jsonprettyprinter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import static java.lang.Character.isWhitespace;
import static java.util.Objects.requireNonNull;

/**
 * This is an auxiliary class for {@link JsonPrettyPrinter}.
 * It reads and breaks a <b>valid</b> JSON string into tokens.
 * <p>
 * The tokens are stored in an internal buffer from where they can be read
 * by calling the {@link JsonTokenizer#iterator()} method or using an enhanced {@code for} statement.
 */
public class JsonTokenizer implements Iterable<String> {

    private static final String STRUCTURAL_CHARACTERS = "{}[]:,";
    private static final char QUOTE = '\"';
    private static final char ESCAPE_CHARACTER = '\\';

    private final Deque<String> tokens = new ArrayDeque<>();
    private final StringBuilder currentToken = new StringBuilder();

    private boolean betweenQuotes;
    private boolean escapedChar;

    /**
     * Creates a new instance by tokenizing the specified JSON string.
     * The string is not validated: passing an invalid JSON string may result in undefined behaviour.
     *
     * @param json the valid JSON string to be broke into tokens
     * @throws NullPointerException if {@code json} is null
     */
    public JsonTokenizer(String json) {
        requireNonNull(json, "json cannot be null");
        tokenize(json);
    }

    private void tokenize(String json) {
        for (char currentChar : json.toCharArray()) {
            if (escapedChar) {
                escapedChar = false;
                currentToken.append(currentChar);
                continue;
            }

            if (isStructuralCharacter(currentChar)) {
                handleStructuralCharacter(currentChar);
            } else if (currentChar == QUOTE) {
                betweenQuotes = !betweenQuotes;
                currentToken.append(currentChar);
            } else if (currentChar == ESCAPE_CHARACTER) {
                escapedChar = true;
                currentToken.append(currentChar);
            } else {
                handleOtherCharacters(currentChar);
            }
        }
    }

    private boolean isStructuralCharacter(char c) {
        return STRUCTURAL_CHARACTERS.indexOf(c) > -1;
    }

    private void handleStructuralCharacter(char currentChar) {
        if (betweenQuotes) {
            currentToken.append(currentChar);
        } else {
            if (currentTokenIsNotEmpty()) {
                tokens.add(currentToken.toString());
                currentToken.setLength(0);
            }
            tokens.add(String.valueOf(currentChar));
        }
    }

    private boolean currentTokenIsNotEmpty() {
        return currentToken.length() != 0;
    }

    private void handleOtherCharacters(char currentChar) {
        if (isNotWhitespace(currentChar) || betweenQuotes) {
            currentToken.append(currentChar);
        }
    }

    private boolean isNotWhitespace(char currentChar) {
        return !isWhitespace(currentChar);
    }

    /**
     * Returns an iterator over the tokens in the internal buffer.
     *
     * @return an iterator over the tokens in the internal buffer
     */
    @Override
    public Iterator<String> iterator() {
        return tokens.iterator();
    }
}
