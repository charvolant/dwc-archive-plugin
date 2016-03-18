package au.org.ala.data.dwca

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.gbif.dwc.terms.DwcTerm
import org.springframework.web.multipart.commons.CommonsMultipartFile
import spock.lang.Specification


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
@TestMixin(ControllerUnitTestMixin)
class CheckConfigurationSpec extends Specification {
    def setup() {
        mockCommandObject(CheckConfiguration)
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


    void "test validation 1"() {
        when:
        def config = new CheckConfiguration(source: new URL('http://host/path/archive.zip'))
        def valid = config.validate()
        then:
        valid
    }


    void "test validation 2"() {
        when:
        def config = new CheckConfiguration(sourceFile: Mock(CommonsMultipartFile.class))
        def valid = config.validate()
        then:
        valid
    }



    void "test validation 3"() {
        when:
        def config = new CheckConfiguration()
        def valid = config.validate()
        then:
        !valid
    }

}