package au.org.ala.data.filter

import au.org.ala.util.AlaTerm
import org.gbif.dwc.terms.DcTerm
import org.gbif.dwc.terms.DwcTerm
import org.gbif.dwc.terms.Term
import org.gbif.dwc.text.Archive

import java.text.ParseException
import au.org.ala.util.Tokenizer

/**
 * Parse a filter expression.
 * <p>
 *     Uses a simple recursive descent parser.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class FilterParser {
    static enum Token {
        STRING,
        NUMBER,
        IDENTIFIER,
        EQUALS,
        DOT,
        AND,
        OR,
        NOT,
        OPEN_PAREN,
        CLOSE_PAREN
    }
    static KEYWORDS = ['==': Token.EQUALS, '.': Token.DOT, and: Token.AND, or: Token.OR, not: Token.NOT, '(': Token.OPEN_PAREN, ')': Token.CLOSE_PAREN ]

    Tokenizer<Token> tokenizer

    FilterParser(String expression) {
        tokenizer = new Tokenizer<Integer>(expression, Token.STRING, Token.NUMBER, Token.IDENTIFIER, KEYWORDS)
    }


    Filter parse() {
        Filter f = parseDisjunction()
        return f
    }

    private Filter parseDisjunction() {
        Filter f = parseConjunction()
        if (tokenizer.token == Token.OR) {
            tokenizer.nextToken()
            Filter fp = parseDisjunction()
            return new OrFilter(or: [f, fp])
        }
        return f
    }

    private Filter parseConjunction() {
        Filter f = parseNegation()
        if (tokenizer.token == Token.AND) {
            tokenizer.nextToken()
            Filter fp = parseConjunction()
            return new AndFilter(and: [f, fp])
        }
        return f
    }

    private Filter parseNegation() {
        if (tokenizer.token == Token.NOT) {
            tokenizer.nextToken()
            Filter fp = parseNegation()
            return new NotFilter(not: fp)
        }
        return parseExpression()
    }

    private Filter parseExpression() {
        if (tokenizer.token == Token.OPEN_PAREN) {
            tokenizer.nextToken()
            Filter fp = parse()
            if (tokenizer.token != Token.CLOSE_PAREN)
                throw new ParseException("Expecting close parenthesis", tokenizer.pos)
            tokenizer.nextToken()
            return fp
        }
        return parseComparison()
    }

    private Filter parseComparison() {
        Term term
        String value
        if (tokenizer.token != Token.IDENTIFIER)
            throw new ParseException("Expecting identifier", tokenizer.pos)
        term = buildTerm(tokenizer.value)
        if (tokenizer.nextToken() != Token.EQUALS)
            throw new ParseException("Expecting equals", tokenizer.pos)
        if (tokenizer.nextToken() != Token.STRING)
            throw new ParseException("Expecting string", tokenizer.pos)
        value = tokenizer.value
        tokenizer.nextToken()
        return new ValueFilter(column: term, value: value)

    }

    Term buildTerm(String name) {

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
}
