package nemethi.jsonprettyprinter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Tests using example JSON documents from https://json.org/example.html")
class JsonPrettyPrinterIT {

    @ParameterizedTest
    @MethodSource("testArguments")
    void prettyPrintOfficialJsonExamples(String input, String expected) {
        JsonPrettyPrinter printer = new JsonPrettyPrinter();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        printer.prettyPrint(input, output);

        String actual = normalizeLineEndings(output.toString());
        assertEquals(expected, actual);
    }

    private static Stream<? extends Arguments> testArguments() throws Exception {
        return Stream.of(
                arguments(file("input1.json"), file("expected1.json")),
                arguments(file("input2.json"), file("expected2.json")),
                arguments(file("input3.json"), file("expected3.json")),
                arguments(file("input4.json"), file("expected4.json")));
    }

    private static String file(String filename) throws IOException {
        String content = Files.readString(Path.of("src", "test", "resources", filename));
        content = normalizeLineEndings(content);
        return content.trim();
    }

    private static String normalizeLineEndings(String s) {
        return s.replaceAll("\\r\\n?", "\n");
    }
}
