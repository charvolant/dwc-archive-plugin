package au.org.ala.data.dwca

import grails.test.mixin.TestFor
import org.gbif.dwc.terms.DwcTerm
import spock.lang.Specification


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class CheckConfigurationSpec extends Specification {
    def setup() {
    }

    def cleanup() {
    }

    void "test get term list 1"() {
        when:
            def config = new CheckConfiguration()
        then:
            config.getUniqueTermList() == "catalogNumber"
    }

    void "test get term list 2"() {
        when:
        def config = new CheckConfiguration()
        config.uniqueTerms = [DwcTerm.collectionCode, DwcTerm.catalogNumber]
        then:
        config.getUniqueTermList() == "collectionCode, catalogNumber"
    }

    void "test set term list 1"() {
        when:
        def config = new CheckConfiguration()
        config.setUniqueTermList("catalogNumber")
        then:
        config.uniqueTerms == [ DwcTerm.catalogNumber ]
    }

    void "test set term list 2"() {
        when:
        def config = new CheckConfiguration()
        config.setUniqueTermList("collectionCode, catalogNumber")
        then:
        config.uniqueTerms == [ DwcTerm.collectionCode, DwcTerm.catalogNumber ]
    }

    void "test set term list 3"() {
        when:
        def config = new CheckConfiguration()
        config.setUniqueTermList("collectionCode, catalogNumber, xxx")
        then:
        thrown IllegalArgumentException
    }

}