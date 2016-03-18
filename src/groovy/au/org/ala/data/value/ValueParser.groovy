package au.org.ala.data.value

import au.org.ala.data.filter.*
import au.org.ala.util.AlaTerm
import au.org.ala.util.Tokenizer
import org.gbif.dwc.terms.DcTerm
import org.gbif.dwc.terms.DwcTerm
import org.gbif.dwc.terms.Term

import java.text.ParseException

/**
 * Parse a value expression.
 * <p>
 * Uses a simple recursive descent parser.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class ValueParser {
    static enum Token {
        STRING,
        NUMBER,
        IDENTIFIER,
        ASSIGN,
        DOT,
        COMMA
    }
    static KEYWORDS = ['=': Token.ASSIGN, '.': Token.DOT, ',': Token.COMMA ]

    Tokenizer<Token> tokenizer

    ValueParser(String expression) {
        tokenizer = new Tokenizer<Integer>(expression, Token.STRING, Token.NUMBER, Token.IDENTIFIER, KEYWORDS)
    }


    List<Value> parse() {
        return parseList()
    }

    private List<Value> parseList() {
        List<Value> values = []
        Value v = parseAssignment()
        values.add(v)
        while (tokenizer.token == Token.COMMA) {
            tokenizer.nextToken()
            v = parseAssignment()
            values.add(v)
        }
        return values
    }

    private Value parseAssignment() {
        String term
        String value
        if (tokenizer.token != Token.IDENTIFIER)
            throw new ParseException("Expecting identifier", tokenizer.pos)
        term = tokenizer.value
        if (tokenizer.nextToken() != Token.ASSIGN)
            throw new ParseException("Expecting assignment", tokenizer.pos)
        if (tokenizer.nextToken() != Token.STRING)
            throw new ParseException("Expecting string", tokenizer.pos)
        value = tokenizer.value
        tokenizer.nextToken()
        return new Value(term, value)

    }

    static Term buildTerm(String name) {
        try {
            return DwcTerm.valueOf(name)
        } catch (IllegalArgumentException ex) {
        }
        try {
            return DcTerm.valueOf(name)
        } catch (IllegalArgumentException ex) {
        }
        return new AlaTerm(term: name)
    }

    static String asString(List<Value> values) {
        return !values ? "" : values.inject("", { str, val -> str = str + (str.isEmpty() ? "" : ", ") + val.term.simpleName() + " = \"" + val.value + "\"" })
    }
}
