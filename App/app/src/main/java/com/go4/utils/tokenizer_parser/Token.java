package com.go4.utils.tokenizer_parser;

import java.util.Objects;

/**
 * @author u8003980 Chan Cheng Leong
 */
public class Token {
    // The following enum defines different types of tokens. Example of accessing these: Token.Type.INT
    public enum Type {LOCATION, YEAR, MONTH, DATE, TIME, SEPARATOR, INVALID};

    public static class IllegalTokenException extends IllegalArgumentException {
        public IllegalTokenException(String errorMessage) {
            super(errorMessage);
        }
    }

    // Fields of the class Token.
    private final String token;
    private final Type type;
    private final int length;

    public Token(String token, Type type, int length) {
        this.token = token;
        this.type = type;
        this.length = length;
    }

    public String getToken() {
        return token;
    }

    public Type getType() {
        return type;
    }

    public int getLength() { return length; }

    @Override
    public String toString() {
        return type + "";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Token)) return false;
        return this.type == ((Token) other).getType() && this.token.equals(((Token) other).getToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, type);
    }
}
