package au.org.ala.util

import java.text.ParseException
import java.util.regex.Pattern

/**
 * Tokenize into basic lexemes.
 * <p>
 * Strings are enclosed in double quotes.
 * Identifier sequences are treated as identifiers.
 * <p>
 * Keywords (and punctuation) are matched eagerly.
 * <p>
 * Note that this is a very imperfect lexer.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class Tokenizer<T> {
    static char QUOTE = '"'
    static char ESCAPE = '\\'

    Map<String, T> keywords
    /** The reader to tokenize */
    Reader reader
    /** The current character */
    int ch
    /** The position in the stream */
    int pos
    /** The token value (what was actually read) */
    String value
    /** The current token */
    T token
    /** The token that represents a string */
    T string
    /** The token that represents a number */
    T number
    /** The token that represents an identifier */
    T identifier

    Tokenizer(Reader reader, T string, T number, T identifier, Map<String, T> keywords) {
        this.reader = reader
        this.string = string
        this.number = number
        this.identifier = identifier
        this.keywords = keywords
        pos = -1
        ch = read()
        nextToken()
    }

    Tokenizer(String expression, T string, T number, T identifier, Map<String, T> keywords) {
        this(new StringReader(expression), string, number, identifier, keywords)
    }


    T nextToken() {
        StringBuffer buffer = new StringBuffer(32);
        while (ch != -1 && Character.isSpaceChar(ch)) ch = read()
        if (ch == -1) {
            token = null
            value = null
            return token
        }
        if (ch == QUOTE) {
            ch = reader.read()
            while (ch != -1 && ch != QUOTE) {
                if (ch == ESCAPE)
                    ch = read()
                buffer.append((char) ch)
                ch = read()
            }
            if (ch == -1)
                throw new ParseException("No close quote", 0)
            ch = read()
            token = string
            value = buffer.toString()
            return token
        }
        if (Character.isDigit(ch)) {
            while (ch != -1 && Character.isDigit(ch)) {
                 buffer.append((char) ch)
                ch = read()
            }
            token = number
            value = buffer.toString()
            return token
        }
        while (ch != -1 && !Character.isSpaceChar(ch) && !keywords.containsKey(buffer.toString())) {
            buffer.append((char) ch)
            ch = read()
        }
        value = buffer.toString()
        token = identifier
        if (keywords.containsKey(value))
            token = keywords.get(value)
        return token
    }

    private int read() {
        pos++
        return reader.read()
    }
}

