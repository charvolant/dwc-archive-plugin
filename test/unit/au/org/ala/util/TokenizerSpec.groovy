package au.org.ala.util

import org.gbif.dwc.terms.DwcTerm
import spock.lang.Specification


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class TokenizerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test tokenize 1"() {
        when:
        def tokenizer = new Tokenizer<Integer>(' "This is a string" ', 1, 2, 3, [:])
        then:
        tokenizer.token == 1
        tokenizer.value == 'This is a string'
        tokenizer.nextToken() == null
    }

    void "test tokenize 2"() {
        when:
        def tokenizer = new Tokenizer<Integer>(' "\\"This is a string\\"" ', 1, 2, 3, [:])
        then:
        tokenizer.token == 1
        tokenizer.value == '"This is a string"'
        tokenizer.nextToken() == null
    }

    void "test tokenize 3"() {
        when:
        def tokenizer = new Tokenizer<Integer>(' 1234 ', 1, 2, 3, [:])
        then:
        tokenizer.token == 2
        tokenizer.value == '1234'
        tokenizer.nextToken() == null
    }

    void "test tokenize 4"() {
        when:
        def tokenizer = new Tokenizer<Integer>(' 1234 4567 ', 1, 2, 3, [:])
        then:
        tokenizer.token == 2
        tokenizer.value == '1234'
        tokenizer.nextToken() == 2
        tokenizer.value == '4567'
    }

    void "test tokenize 5"() {
        when:
        def tokenizer = new Tokenizer<Integer>(' hello ', 1, 2, 3, [:])
        then:
        tokenizer.token == 3
        tokenizer.value == 'hello'
        tokenizer.nextToken() == null
    }

    void "test tokenize 6"() {
        when:
        def tokenizer = new Tokenizer<Integer>('http://localhost:8080/#thing ', 1, 2, 3, [:])
        then:
        tokenizer.token == 3
        tokenizer.value == 'http://localhost:8080/#thing'
        tokenizer.nextToken() == null
    }

    void "test tokenize 7"() {
        when:
        def tokenizer = new Tokenizer<Integer>(' hello hello ', 1, 2, 3, [hello: 4])
        then:
        tokenizer.token == 4
        tokenizer.value == 'hello'
        tokenizer.nextToken() == 4
    }

    void "test tokenize 8"() {
        when:
        def tokenizer = new Tokenizer<Integer>(' alpha == "Omega" ', 1, 2, 3, ['==': 4])
        then:
        tokenizer.token == 3
        tokenizer.value == 'alpha'
        tokenizer.nextToken() == 4
        tokenizer.value == '=='
        tokenizer.nextToken() == 1
        tokenizer.value == 'Omega'
        tokenizer.nextToken() == null
    }

    void "test tokenize 9"() {
        when:
        def tokenizer = new Tokenizer<Integer>(' alpha .= "Omega" ', 1, 2, 3, ['=': 4, '.': 5])
        then:
        tokenizer.token == 3
        tokenizer.value == 'alpha'
        tokenizer.nextToken() == 5
        tokenizer.value == '.'
        tokenizer.nextToken() == 4
        tokenizer.value == '='
        tokenizer.nextToken() == 1
        tokenizer.value == 'Omega'
        tokenizer.nextToken() == null
    }

}