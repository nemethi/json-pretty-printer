package nemethi.jsonprettyprinter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;


class JsonTokenizerTest {

    private JsonTokenizer tokenizer;

    @Test
    void nullJsonString() {
        assertThrows(NullPointerException.class,
                () -> new JsonTokenizer(null),
                "json cannot be null");
    }

    @Test
    void emptyJsonString() {
        tokenizer = new JsonTokenizer("");

        assertEquals(emptyList(), getTokens());
    }

    @ParameterizedTest(name = "{index} - {2}")
    @MethodSource("arrayOfValuesArguments")
    void jsonArrays(String json, List<String> expected, String testName) {
        tokenizer = new JsonTokenizer(json);

        assertEquals(expected, getTokens());
    }

    private static Stream<Arguments> arrayOfValuesArguments() {
        return Stream.of(
                arguments("[]", List.of("[", "]"), "Empty array"),
                arguments("[\"a\",\"b\"]", List.of("[", "\"a\"", ",", "\"b\"", "]"), "String values"),
                arguments("[1,2]", List.of("[", "1", ",", "2", "]"), "Integer values"),
                arguments("[1.1,1.2]", List.of("[", "1.1", ",", "1.2", "]"), "Float values"),
                arguments("[true,false]", List.of("[", "true", ",", "false", "]"), "Boolean values"),
                arguments("[null,null]", List.of("[", "null", ",", "null", "]"), "Null values"),
                arguments("[[1,2],[3,4]]", List.of("[", "[", "1", ",", "2", "]", ",", "[", "3", ",", "4", "]", "]"), "Array values"),
                arguments("[{\"key\":\"value\"},{\"key2\":\"value2\"}]", List.of("[", "{", "\"key\"", ":", "\"value\"", "}", ",", "{", "\"key2\"", ":", "\"value2\"", "}", "]"), "Object values"));
    }

    @ParameterizedTest(name = "{index} - {2}")
    @MethodSource("objectOfValuesArguments")
    void jsonObjects(String json, List<String> expected, String testName) {
        tokenizer = new JsonTokenizer(json);

        assertEquals(expected, getTokens());
    }

    private static Stream<Arguments> objectOfValuesArguments() {
        return Stream.of(
                arguments("{}", List.of("{", "}"), "Empty object"),
                arguments("{\"key\":\"value\"}", List.of("{", "\"key\"", ":", "\"value\"", "}"), "String value"),
                arguments("{\"key\":1}", List.of("{", "\"key\"", ":", "1", "}"), "Integer value"),
                arguments("{\"key\":1.5}", List.of("{", "\"key\"", ":", "1.5", "}"), "Float value"),
                arguments("{\"key\":false}", List.of("{", "\"key\"", ":", "false", "}"), "Boolean value"),
                arguments("{\"key\":null}", List.of("{", "\"key\"", ":", "null", "}"), "Null value"),
                arguments("{\"key\":[1,2]}", List.of("{", "\"key\"", ":", "[", "1", ",", "2", "]", "}"), "Array value"),
                arguments("{\"key\":{\"innerKey\":\"value\"}}", List.of("{", "\"key\"", ":", "{", "\"innerKey\"", ":", "\"value\"", "}", "}"), "Object value"));
    }

    @ParameterizedTest(name = "{index} - {2}")
    @MethodSource("specialCharInValueArguments")
    void specialCharInValue(String json, List<String> expected, String testName) {
        tokenizer = new JsonTokenizer(json);

        assertEquals(expected, getTokens());
    }

    private static Stream<Arguments> specialCharInValueArguments() {
        return Stream.of(
                arguments("{\"key\":\"value1,value2\"}", List.of("{", "\"key\"", ":", "\"value1,value2\"", "}"), "Value contains comma"),
                arguments("{\"key\":\"value1:value2\"}", List.of("{", "\"key\"", ":", "\"value1:value2\"", "}"), "Value contains colon"),
                arguments("{\"key\":\"value1{value2\"}", List.of("{", "\"key\"", ":", "\"value1{value2\"", "}"), "Value contains opening brace"),
                arguments("{\"key\":\"value1}value2\"}", List.of("{", "\"key\"", ":", "\"value1}value2\"", "}"), "Value contains closing brace"),
                arguments("{\"key\":\"value1[value2\"}", List.of("{", "\"key\"", ":", "\"value1[value2\"", "}"), "Value contains opening bracket"),
                arguments("{\"key\":\"value1]value2\"}", List.of("{", "\"key\"", ":", "\"value1]value2\"", "}"), "Value contains closing bracket"),
                arguments("{\"key\":\"value1\\\"value2\"}", List.of("{", "\"key\"", ":", "\"value1\\\"value2\"", "}"), "Value contains escaped quote"),
                arguments("{\"key\":\"value1\\\\value2\"}", List.of("{", "\"key\"", ":", "\"value1\\\\value2\"", "}"), "Value contains escaped backslash"));
    }

    @ParameterizedTest(name = "{index} - {2}")
    @MethodSource("whitespaceInJsonArguments")
    void whitespaceInJson(String json, List<String> expected, String testName) {
        tokenizer = new JsonTokenizer(json);

        assertEquals(expected, getTokens());
    }

    private static Stream<Arguments> whitespaceInJsonArguments() {
        return Stream.of(
                arguments("{ \"key\" : [ \"value\" ] }", List.of("{", "\"key\"", ":", "[", "\"value\"", "]", "}"), "Extra spaces"),
                arguments("{\" key \":[\" value \"]}", List.of("{", "\" key \"", ":", "[", "\" value \"", "]", "}"), "Spaces in strings"),
                arguments("{\r\n\"key\"\r\n:\r\n[\r\n\"value\"\r\n]\r\n}", List.of("{", "\"key\"", ":", "[", "\"value\"", "]", "}"), "Extra Windows line breaks"),
                arguments("{\"\r\nkey\r\n\":[\"\r\nvalue\r\n\"]}", List.of("{", "\"\r\nkey\r\n\"", ":", "[", "\"\r\nvalue\r\n\"", "]", "}"), "Windows line break in strings"),
                arguments("{\n\"key\"\n:\n[\n\"value\"\n]\n}", List.of("{", "\"key\"", ":", "[", "\"value\"", "]", "}"), "Extra *nix line breaks"),
                arguments("{\"\nkey\n\":[\"\nvalue\n\"]}", List.of("{", "\"\nkey\n\"", ":", "[", "\"\nvalue\n\"", "]", "}"), "*nix line breaks in strings"),
                arguments("{\t\"key\"\t:\t[\t\"value\"\t]\t}", List.of("{", "\"key\"", ":", "[", "\"value\"", "]", "}"), "Extra tabs"),
                arguments("{\"\tkey\t\":[\"\tvalue\t\"]}", List.of("{", "\"\tkey\t\"", ":", "[", "\"\tvalue\t\"", "]", "}"), "Tabs in strings"));
    }

    private List<String> getTokens() {
        return stream(spliteratorUnknownSize(tokenizer.iterator(), Spliterator.ORDERED), false)
                .collect(toList());
    }
}
