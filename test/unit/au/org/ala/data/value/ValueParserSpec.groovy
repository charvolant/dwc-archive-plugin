package au.org.ala.data.value

import au.org.ala.data.filter.*
import spock.lang.Specification

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class ValueParserSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test parse 1"() {
        when:
        def values = new ValueParser('hello = "2"').parse()
        then:
        values.size() == 1
        def value = values[0]
        value instanceof Value
        value.term.simpleName() == 'hello'
        value.value == '2'
    }


    void "test parse 2"() {
        when:
        def values = new ValueParser('hello = "2", goodbye = "3"').parse()
        then:
        values.size() == 2
        def value1 = values[0]
        value1 instanceof Value
        value1.term.simpleName() == 'hello'
        value1.value == '2'
        def value2 = values[1]
        value2 instanceof Value
        value2.term.simpleName() == 'goodbye'
        value2.value == '3'
    }
}