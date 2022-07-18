package nemethi.jsonprettyprinter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonPrettyPrinterTest {

    private JsonPrettyPrinter printer;
    private StringWriter writer;

    @BeforeEach
    void setUp() {
        printer = new JsonPrettyPrinter();
        writer = new StringWriter();
    }

    @Test
    void defaultIndentAmountIsTwo() {
        assertEquals(2, printer.getIndentAmount());
    }

    @Test
    void indentAmountCanBeSpecified() {
        final int indentAmount = 4;

        printer = new JsonPrettyPrinter(indentAmount);

        assertEquals(indentAmount, printer.getIndentAmount());
    }

    @Test
    void indentAmountCannotBeNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new JsonPrettyPrinter(-1),
                "indentAmount must be between 0 and 10 inclusive");
    }

    @Test
    void indentAmountCannotBeGreaterThanTen() {
        assertThrows(IllegalArgumentException.class,
                () -> new JsonPrettyPrinter(11),
                "indentAmount must be between 0 and 10 inclusive");
    }

    @Test
    void nullJson() {
        assertThrows(NullPointerException.class, () -> printer.prettyPrint(null, writer));
    }

    @Test
    void emptyJson() {
        printer.prettyPrint("", writer);
        assertOutput("");
    }

    @Test
    void nullWriter() {
        assertThrows(NullPointerException.class,
                () -> printer.prettyPrint("", (Writer) null),
                "writer cannot be null");
    }

    @Test
    void nullOutputStream() {
        assertThrows(NullPointerException.class,
                () -> printer.prettyPrint("", (OutputStream) null),
                "outputStream cannot be null");
    }

    @Nested
    class Arrays {
        @Test
        void emptyArray() {
            printer.prettyPrint("[]", writer);
            assertOutput("[]");
        }

        @Test
        void singleElementArray() {
            printer.prettyPrint("[1]", writer);
            assertOutput("[\n  1\n]");
        }

        @Test
        void singleElementArrayWithEmptyArray() {
            printer.prettyPrint("[[]]", writer);
            assertOutput("[\n  []\n]");
        }

        @Test
        void multiElementArray() {
            printer.prettyPrint("[1,2]", writer);
            assertOutput("[\n  1,\n  2\n]");
        }

        @Test
        void multiElementArrayWithEmptyArrays() {
            printer.prettyPrint("[[],1,[]]", writer);
            assertOutput("[\n  [],\n  1,\n  []\n]");
        }

        @Test
        void singleElementNestedArray() {
            printer.prettyPrint("[[1]]", writer);
            assertOutput("[\n  [\n    1\n  ]\n]");
        }

        @Test
        void singleElementNestedArrays() {
            printer.prettyPrint("[[1],[2]]", writer);
            assertOutput("[\n  [\n    1\n  ],\n  [\n    2\n  ]\n]");
        }

        @Test
        void singleElementNestedArraysWithEmptyArray() {
            printer.prettyPrint("[[1],[],[2]]", writer);
            assertOutput("[\n  [\n    1\n  ],\n  [],\n  [\n    2\n  ]\n]");
        }

        @Test
        void multiElementNestedArrays() {
            printer.prettyPrint("[[1,2],[3,4]]", writer);
            assertOutput("[\n  [\n    1,\n    2\n  ],\n  [\n    3,\n    4\n  ]\n]");
        }

        @Test
        void multiElementNestedArraysWithEmptyArray() {
            printer.prettyPrint("[[1,2],[],[3,4]]", writer);
            assertOutput("[\n  [\n    1,\n    2\n  ],\n  [],\n  [\n    3,\n    4\n  ]\n]");
        }
    }

    @Nested
    class Objects {
        @Test
        void emptyObject() {
            printer.prettyPrint("{}", writer);
            assertOutput("{}");
        }

        @Test
        void singleElementObject() {
            printer.prettyPrint("{\"key\":1}", writer);
            assertOutput("{\n  \"key\": 1\n}");
        }

        @Test
        void singleElementObjectWithEmptyObject() {
            printer.prettyPrint("{\"key\":{}}", writer);
            assertOutput("{\n  \"key\": {}\n}");
        }

        @Test
        void multiElementObject() {
            printer.prettyPrint("{\"key1\":1,\"key2\":2}", writer);
            assertOutput("{\n  \"key1\": 1,\n  \"key2\": 2\n}");
        }

        @Test
        void multiElementObjectWithEmptyObjects() {
            printer.prettyPrint("{\"key1\":{},\"key2\":2,\"key3\":{}}", writer);
            assertOutput("{\n  \"key1\": {},\n  \"key2\": 2,\n  \"key3\": {}\n}");
        }

        @Test
        void singleElementNestedObject() {
            printer.prettyPrint("{\"keyA\":{\"key1\":1}}", writer);
            assertOutput("{\n  \"keyA\": {\n    \"key1\": 1\n  }\n}");
        }

        @Test
        void singleElementNestedObjects() {
            printer.prettyPrint("{\"keyA\":{\"key1\":1},\"keyB\":{\"key2\":2}}", writer);
            assertOutput("{\n  \"keyA\": {\n    \"key1\": 1\n  },\n  \"keyB\": {\n    \"key2\": 2\n  }\n}");
        }

        @Test
        void singleElementNestedObjectsWithEmptyObject() {
            printer.prettyPrint("{\"keyA\":{\"key1\":1},\"keyB\":{},\"keyC\":{\"key2\":2}}", writer);
            assertOutput("{\n  \"keyA\": {\n    \"key1\": 1\n  },\n  \"keyB\": {},\n  \"keyC\": {\n    \"key2\": 2\n  }\n}");
        }

        @Test
        void multiElementNestedObjects() {
            printer.prettyPrint("{\"keyA\":{\"key1\":1,\"key2\":2},\"keyB\":{\"key3\":3,\"key4\":4}}", writer);
            assertOutput("{\n  \"keyA\": {\n    \"key1\": 1,\n    \"key2\": 2\n  },\n  \"keyB\": {\n    \"key3\": 3,\n    \"key4\": 4\n  }\n}");
        }

        @Test
        void multiElementNestedObjectsWithEmptyObject() {
            printer.prettyPrint("{\"keyA\":{\"key1\":1,\"key2\":2},\"keyB\":{},\"keyC\":{\"key3\":3,\"key4\":4}}", writer);
            assertOutput("{\n  \"keyA\": {\n    \"key1\": 1,\n    \"key2\": 2\n  },\n  \"keyB\": {},\n  \"keyC\": {\n    \"key3\": 3,\n    \"key4\": 4\n  }\n}");
        }
    }

    @Nested
    class ArraysMixedWithObjects {
        @Test
        void singleElementArrayWithEmptyObject() {
            printer.prettyPrint("[{}]", writer);
            assertOutput("[\n  {}\n]");
        }

        @Test
        void singleElementArrayWithSingleElementObject() {
            printer.prettyPrint("[{\"key\":1}]", writer);
            assertOutput("[\n  {\n    \"key\": 1\n  }\n]");
        }

        @Test
        void singleElementArrayWithMultiElementObject() {
            printer.prettyPrint("[{\"key1\":1,\"key2\":2}]", writer);
            assertOutput("[\n  {\n    \"key1\": 1,\n    \"key2\": 2\n  }\n]");
        }

        @Test
        void singleElementArrayWithSingleElementNestedEmptyObject() {
            printer.prettyPrint("[{\"key\":{}}]", writer);
            assertOutput("[\n  {\n    \"key\": {}\n  }\n]");
        }

        @Test
        void singleElementArrayWithSingleElementNestedObject() {
            printer.prettyPrint("[{\"key1\":{\"key2\":2}}]", writer);
            assertOutput("[\n  {\n    \"key1\": {\n      \"key2\": 2\n    }\n  }\n]");
        }

        @Test
        void singleElementArrayWithMultiElementNestedObject() {
            printer.prettyPrint("[{\"keyA\":{\"key1\":1,\"key2\":2},\"keyB\":{\"key3\":3,\"key4\":4}}]", writer);
            assertOutput("[\n  {\n    \"keyA\": {\n      \"key1\": 1,\n      \"key2\": 2\n    },\n    \"keyB\": {\n      \"key3\": 3,\n      \"key4\": 4\n    }\n  }\n]");
        }

        @Test
        void multiElementArrayWithMultiElementObjects() {
            printer.prettyPrint("[{\"key1\":1,\"key2\":2},{\"key3\":3,\"key4\":4}]", writer);
            assertOutput("[\n  {\n    \"key1\": 1,\n    \"key2\": 2\n  },\n  {\n    \"key3\": 3,\n    \"key4\": 4\n  }\n]");
        }

        @Test
        void singleElementObjectWithEmptyArray() {
            printer.prettyPrint("{\"key\":[]}", writer);
            assertOutput("{\n  \"key\": []\n}");
        }

        @Test
        void singleElementObjectWithSingleElementArray() {
            printer.prettyPrint("{\"key\":[1]}", writer);
            assertOutput("{\n  \"key\": [\n    1\n  ]\n}");
        }

        @Test
        void singleElementObjectWithMultiElementArray() {
            printer.prettyPrint("{\"key\":[1,2]}", writer);
            assertOutput("{\n  \"key\": [\n    1,\n    2\n  ]\n}");
        }

        @Test
        void singleElementObjectWithSingleElementNestedEmptyArray() {
            printer.prettyPrint("{\"key\":[[]]}", writer);
            assertOutput("{\n  \"key\": [\n    []\n  ]\n}");
        }

        @Test
        void singleElementObjectWithSingleElementNestedArray() {
            printer.prettyPrint("{\"key\":[[1]]}", writer);
            assertOutput("{\n  \"key\": [\n    [\n      1\n    ]\n  ]\n}");
        }

        @Test
        void singleElementObjectWithMultiElementNestedArray() {
            printer.prettyPrint("{\"key\":[[1,2],[3,4]]}", writer);
            assertOutput("{\n  \"key\": [\n    [\n      1,\n      2\n    ],\n    [\n      3,\n      4\n    ]\n  ]\n}");
        }

        @Test
        void multiElementObjectWithMultiElementArrays() {
            printer.prettyPrint("{\"key1\":[1,2],\"key2\":[3,4]}", writer);
            assertOutput("{\n  \"key1\": [\n    1,\n    2\n  ],\n  \"key2\": [\n    3,\n    4\n  ]\n}");
        }

        @Test
        void arrayContainingNestedObjectsAndArrays() {
            printer.prettyPrint("[{\"key1\":{\"key2\":[{}]}},[{},[]],[1,[{\"key3\":{}}]]]", writer);
            assertOutput("[\n  {\n    \"key1\": {\n      \"key2\": [\n        {}\n      ]\n    }\n  },\n  [\n    {},\n    []\n  ],\n  [\n    1,\n    [\n      {\n        \"key3\": {}\n      }\n    ]\n  ]\n]");
        }

        @Test
        void objectContainingNestedObjectsAndArrays() {
            printer.prettyPrint("{\"key1\":[1,[2,{\"key2\":[]}]],\"key3\":[{},[]],\"key4\":{\"key5\":3,\"key6\":{\"key7\":[4,[]]}}}", writer);
            assertOutput("{\n  \"key1\": [\n    1,\n    [\n      2,\n      {\n        \"key2\": []\n      }\n    ]\n  ],\n  \"key3\": [\n    {},\n    []\n  ],\n  \"key4\": {\n    \"key5\": 3,\n    \"key6\": {\n      \"key7\": [\n        4,\n        []\n      ]\n    }\n  }\n}");
        }

        @Test
        void arrayContainingAllDataTypes() {
            printer.prettyPrint("[null,true,-1,3.14,\"value\",[],{},[1,2],{\"key\":\"value\"}]", writer);
            assertOutput("[\n  null,\n  true,\n  -1,\n  3.14,\n  \"value\",\n  [],\n  {},\n  [\n    1,\n    2\n  ],\n  {\n    \"key\": \"value\"\n  }\n]");
        }

        @Test
        void objectContainingAllDataTypes() {
            printer.prettyPrint("{\"null\":null,\"boolean\":true,\"integer\":-1,\"floatingPointNumber\":3.14,\"string\":\"value\",\"emptyArray\":[],\"emptyObject\":{},\"array\":[1,2],\"object\":{\"key\":\"value\"}}", writer);
            assertOutput("{\n  \"null\": null,\n  \"boolean\": true,\n  \"integer\": -1,\n  \"floatingPointNumber\": 3.14,\n  \"string\": \"value\",\n  \"emptyArray\": [],\n  \"emptyObject\": {},\n  \"array\": [\n    1,\n    2\n  ],\n  \"object\": {\n    \"key\": \"value\"\n  }\n}");
        }
    }

    @Nested
    class OtherTests {
        private static final String JSON = "{\"key\":{\"key2\":\"value\"}}";
        private ByteArrayOutputStream outputStream;

        @BeforeEach
        void setUp() {
            outputStream = new ByteArrayOutputStream();
        }

        @Test
        void printsTheSameToWriterAndOutputStream() {
            final String expected = "{\n  \"key\": {\n    \"key2\": \"value\"\n  }\n}";

            printer.prettyPrint(JSON, writer);
            printer.prettyPrint(JSON, outputStream);

            assertEquals(expected, normalizeLineEndings(writer.toString()));
            assertEquals(expected, normalizeLineEndings(outputStream.toString()));
        }

        @Test
        void usesSpecifiedIndentAmount() {
            final int indentAmount = 4;
            final String expected = "{\n    \"key\": {\n        \"key2\": \"value\"\n    }\n}";

            printer = new JsonPrettyPrinter(indentAmount);
            printer.prettyPrint(JSON, outputStream);

            assertEquals(expected, normalizeLineEndings(outputStream.toString()));
        }

        @Test
        void doesNotCloseWriter() {
            AtomicBoolean closed = new AtomicBoolean(false);

            printer.prettyPrint("{}", new Writer() {
                @Override
                public void write(char[] buffer, int off, int len) {
                }

                @Override
                public void flush() {
                }

                @Override
                public void close() {
                    closed.set(true);
                }
            });

            assertFalse(closed.get(), "assert writer is not closed");
        }

        @Test
        void doesNotCloseOutputStream() {
            AtomicBoolean closed = new AtomicBoolean(false);

            printer.prettyPrint("{}", new OutputStream() {
                @Override
                public void write(int b) {
                }

                @Override
                public void close() {
                    closed.set(true);
                }
            });

            assertFalse(closed.get(), "assert outputStream is not closed");
        }
    }

    private void assertOutput(String expected) {
        String actual = normalizeLineEndings(writer.toString());
        assertEquals(expected, actual);
    }

    private String normalizeLineEndings(String s) {
        return s.replaceAll("\\r\\n?", "\n");
    }
}
