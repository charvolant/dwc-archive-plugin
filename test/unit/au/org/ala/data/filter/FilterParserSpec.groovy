package au.org.ala.data.filter

import au.org.ala.util.Tokenizer
import spock.lang.Specification

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class FilterParserSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test parse 1"() {
        when:
        def filter = new FilterParser('hello == "2"').parse()
        then:
        filter instanceof ValueFilter
        filter.column.simpleName() == 'hello'
        filter.value == '2'
    }

    void "test parse 2"() {
        when:
        def filter = new FilterParser('(hello == "2")').parse()
        then:
        filter instanceof ValueFilter
        filter.column.simpleName() == 'hello'
        filter.value == '2'
    }
    
    void "test parse 3"() {
        when:
        def filter = new FilterParser('hello == "2" and goodbye == "nowhere"').parse()
        then:
        filter instanceof AndFilter
        filter.and.size == 2
        filter.and[0] instanceof ValueFilter
        def and0 = filter.and[0]
        and0.column.simpleName() == 'hello'
        and0.value == '2'
        filter.and[1] instanceof ValueFilter
        def and1 = filter.and[1]
        and1.column.simpleName() == 'goodbye'
        and1.value == 'nowhere'
    }

    void "test parse 4"() {
        when:
        def filter = new FilterParser('hello == "2" or goodbye == "nowhere"').parse()
        then:
        filter instanceof OrFilter
        filter.or.size == 2
        filter.or[0] instanceof ValueFilter
        def or0 = filter.or[0]
        or0.column.simpleName() == 'hello'
        or0.value == '2'
        filter.or[1] instanceof ValueFilter
        def or1 = filter.or[1]
        or1.column.simpleName() == 'goodbye'
        or1.value == 'nowhere'
    }

    void "test parse 5"() {
        when:
        def filter = new FilterParser('not hello == "2"').parse()
        then:
        filter instanceof NotFilter
        filter.not instanceof ValueFilter
        def not0 = filter.not
        not0.column.simpleName() == 'hello'
        not0.value == '2'
    }

}