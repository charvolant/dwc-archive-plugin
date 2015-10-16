package au.org.ala.util

import org.gbif.dwc.terms.DwcTerm
import spock.lang.Specification


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class AlaTermSpec extends Specification {
    def setup() {
    }

    def cleanup() {
    }

    void "test term constructor 1"() {
        when:
        def term = new AlaTerm(term: 'hello')
        then:
        term.term == "hello"
        term.uri == URI.create("http://vocabulary.ala.org.au/hello")
        term.measurementType == null
        term.measurementUnit == null
        term.unit == null
        term.qualifiedName() == "http://vocabulary.ala.org.au/hello"
        term.simpleName() == "hello"
    }

    void "test term constructor 2"() {
        when:
        def term = new AlaTerm(uri: URI.create(DwcTerm.basisOfRecord.qualifiedName()))
        then:
        term.term == DwcTerm.basisOfRecord.simpleName()
        term.uri == URI.create(DwcTerm.basisOfRecord.qualifiedName())
        term.measurementType == null
        term.measurementUnit == null
        term.unit == null
        term.qualifiedName() == DwcTerm.basisOfRecord.qualifiedName()
        term.simpleName() == DwcTerm.basisOfRecord.simpleName()
    }

    void "test term getters and setters 1"() {
        when:
        def term = new AlaTerm()
        term.term = "harry"
        then:
        term.term == "harry"
        term.getTerm() == "harry"
        term.uri == URI.create("http://vocabulary.ala.org.au/harry")
        term.getUri() == URI.create("http://vocabulary.ala.org.au/harry")
    }

}