package com.go4.application.historical;

import java.util.Objects;

public class Token {
    // The following enum defines different types of tokens. Example of accessing these: Token.Type.INT
    public enum Type {LOCATION, YEAR, MONTH, DATE, TIME, YEARMONTHDATE, YEARMONTHDATETIME, SEPERATOR}

    /**
     * The following exception should be thrown if a tokenizer attempts to tokenize something that is not of one
     * of the types of tokens.
     */
    public static class IllegalTokenException extends IllegalArgumentException {
        public IllegalTokenException(String errorMessage) {
            super(errorMessage);
        }
    }

    // Fields of the class Token.
    private final String token; // Token representation in String form.
    private final Type type;    // Type of the token.
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
        if (this == other) return true; // Same hashcode.
        if (!(other instanceof Token)) return false; // Null or not the same type.
        return this.type == ((Token) other).getType() && this.token.equals(((Token) other).getToken()); // Values are the same.
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, type);
    }
}
