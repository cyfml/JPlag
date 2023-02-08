package de.jplag.cpp2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.TestAbortedException;

import de.jplag.ParsingException;
import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.TokenPrinter;
import de.jplag.TokenType;

class LanguageTest {

    @Test
    void parse(@TempDir Path tmp) throws IOException, ParsingException {
        Path resolve = tmp.resolve("content.cpp");
        Files.writeString(resolve, """
                class MyClass {
                  public:
                    int myNum;
                    string myString;
                    void newExpressions() {
                      double a[5];
                      double* b = new double[10];
                      MyClass* c = new MyClass();
                      MyClass d;
                    }
                    void myMethod(string value) {
                      int i = 5;
                      i += 10;
                      switch(i) {
                        case 10:
                          i--;
                        case 9:
                          i--;
                          break;
                        default:
                          i = 8;
                      }
                      if (i * 10 > 80) {
                        cout << "Hello World!" << endl;
                      } else {
                        this->myMethod("Oh no");
                      }
                    }

                    void a(string v) {
                      this->myMethod(v);
                    }

                    void b(string v) {
                      MyClass::myMethod(v);
                    }

                    void c(string v) {
                      myMethod(v);
                    }

                    void d(MyClass m, string v) {
                      m.myMethod(v);
                    }

                    void exceptional() {
                      try {
                        int age = 15;
                        if (age >= 18) {
                          cout << "Access granted - you are old enough.";
                        } else {
                          throw (age);
                        }
                      }
                      catch (int myNum) {
                        cout << "Access denied - You must be at least 18 years old.\\n";
                        cout << "Age is: " << myNum;
                      }
                    }
                  private:
                    const long myNum2 = 10;
                };
                enum MyEnum {
                  a,
                  b = 3,
                  c
                };
                """);
        CPPLanguage language = new CPPLanguage();
        List<Token> tokens = language.parse(Set.of(resolve.toFile()));
        System.out.println(TokenPrinter.printTokens(tokens, resolve.toFile()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"i = 10", "i += 10", "i -= 10", "i += 10", "i /= 10", "i %= 10", "i >>= 10", "i <<= 10", "i &= 10", "i ^= 10", "i |= 10",
            "i++", "i--", "++i", "--i",})
    void testAssign(String expr, @TempDir Path path) {
        String function = """
                void f(int i) {
                    %s;
                }
                """.formatted(expr);
        List<Token> all = extractFromString(path, function).tokens();
        List<Token> assignTokens = all.stream().filter(token -> token.getType() == CPPTokenType.ASSIGN).toList();
        assertEquals(1, assignTokens.size());
    }

    @Test
    void testFunctionCall(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                void f() {
                    b->funCall();
                    C::funCall();
                    funCall();
                    a.funCall();
                }
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.FUNCTION_BEGIN, CPPTokenType.APPLY, CPPTokenType.APPLY, CPPTokenType.APPLY, CPPTokenType.APPLY,
                CPPTokenType.FUNCTION_END);
    }

    @Test
    void testLoop(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                void f() {
                    do {
                        goto a;
                    } while (true);

                    a:
                    while (true) {
                        break;
                    }

                    for (;;) {
                        continue;
                    }
                    return;
                }
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.FUNCTION_BEGIN, CPPTokenType.DO_BEGIN, CPPTokenType.GOTO, CPPTokenType.DO_END,
                CPPTokenType.WHILE_BEGIN, CPPTokenType.BREAK, CPPTokenType.WHILE_END, CPPTokenType.FOR_BEGIN, CPPTokenType.CONTINUE,
                CPPTokenType.FOR_END, CPPTokenType.RETURN, CPPTokenType.FUNCTION_END);

    }

    @ParameterizedTest
    @ValueSource(strings = {"this->myMethod(v)", "MyClass::myMethod(v)", "myMethod(v)", "m.myMethod(v)"})
    void testFunctionCalls(String expression, @TempDir Path path) {
        TokenResult result = extractFromString(path, """
                void a(string v) {
                  %s;
                }
                """.formatted(expression));
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.FUNCTION_BEGIN, CPPTokenType.VARDEF, CPPTokenType.APPLY, CPPTokenType.FUNCTION_END);
    }

    @Test
    void testIfElse(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                void f(int a, int b) {
                    int x = 0;
                    int y = 1;
                    if (a < b) {
                        x = 5;
                    } else if (a > b) {
                        y = 10;
                        x = y + b;
                    } else {
                        y = -20;
                    }
                }
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
    }

    @Test
    void testDoubleArrayDeclaration(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                double* b = new double[10];
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
    }

    @Test
    void testDeclaratorList(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                int x, y = 1, z;
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
    }

    @Test
    void testFunctionCallInAssignmentOutsideFunction(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                int x = square(2);
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.VARDEF, CPPTokenType.ASSIGN, CPPTokenType.APPLY);
    }

    @Test
    void testFunctionCallInAssignmentInsideClassOutsideFunction(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                class A {
                    int x = square(3);
                };
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.CLASS_BEGIN, CPPTokenType.VARDEF, CPPTokenType.ASSIGN, CPPTokenType.APPLY,
                CPPTokenType.CLASS_END);
    }

    @Test
    void testUnion(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                union S {
                    std::int32_t n;
                    std::uint16_t s[2];
                    std::uint8_t c;
                };
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
        assertTokenTypes(result.tokens(), CPPTokenType.UNION_BEGIN, CPPTokenType.VARDEF, CPPTokenType.VARDEF, CPPTokenType.VARDEF,
                CPPTokenType.UNION_END);
    }

    @Test
    void testArrayInit(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                class A {    };
                class A {
                };
                int a[] = {1, 2, 3};
                int b[] {1, 2, 3};
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
    }

    @Test
    void varDefs(@TempDir Path path) {
        TokenResult result = extractFromString(path, """
                #include <string>
                #include <iostream>
                #include <vector>
                using namespace std;

                class Hello {

                    int test;

                    public:
                    void say() {
                        vector<string> hellos {"World"};
                        int i = 0;
                        while (i < hellos.size()) {
                            cout << "Hello " + hellos[i];
                            i++;
                        }
                        test = 3;
                    }
                };

                int main() {
                    Hello hello;
                    hello.say();
                    return 0;
                }
                """);
        System.out.println(TokenPrinter.printTokens(result.tokens(), result.file()));
    }

    static void assertTokenTypes(List<Token> tokens, TokenType... types) {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.getType() == SharedTokenType.FILE_END) {
                assertEquals(i, types.length);
                return;
            }
            assertEquals(types[i], token.getType(), "Unexpected token at index " + i);
        }
    }

    TokenResult extractFromString(@TempDir Path path, String content) {
        Path filePath = path.resolve("content.cpp");
        try {
            Files.writeString(filePath, content);
        } catch (IOException e) {
            throw new TestAbortedException("Failed to write temp file", e);
        }
        CPPLanguage language = new CPPLanguage();
        List<Token> tokens;
        try {
            tokens = language.parse(Set.of(filePath.toFile()));
        } catch (ParsingException e) {
            throw new TestAbortedException("Failed to extract tokens", e);
        }
        return new TokenResult(tokens, filePath.toFile());
    }

    record TokenResult(List<Token> tokens, File file) {
    }
}