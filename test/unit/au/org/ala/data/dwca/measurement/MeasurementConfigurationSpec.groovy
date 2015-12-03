package au.org.ala.data.dwca.measurement

import au.org.ala.util.AlaTerm
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.FileItemHeaders
import org.springframework.web.multipart.commons.CommonsMultipartFile
import spock.lang.Specification

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
@TestMixin(ControllerUnitTestMixin)
class MeasurementConfigurationSpec extends Specification {
    def setup() {
        mockCommandObject(MeasurementConfiguration)
    }

    def cleanup() {
    }

    void "test get term map 1"() {
        when:
        def term1 = new AlaTerm(term: 'something', measurementType: 'Something', measurementUnit: 'cm')
        def term2 = new AlaTerm(term: 'nothingMore', measurementType: 'Nothing more')
        def config = new MeasurementConfiguration(terms: [term1, term2])
        then:
        def map = config.termMap
        map.containsKey('Something')
        map['Something'] == term1
        map.containsKey('Nothing more')
        map['Nothing more'] == term2
    }

    void "test get term map 2"() {
        when:
        def term1 = new AlaTerm(term: 'something', measurementType: 'Something', measurementUnit: 'cm')
        def term2 = new AlaTerm(term: 'something', measurementType: 'Something else')
        def config = new MeasurementConfiguration(terms: [term1, term2])
        then:
        def map = config.termMap
        map.containsKey('Something')
        map['Something'] == term1
        map.containsKey('Something else')
        map['Something else'] == term2
    }

    void "test validation 1"() {
        when:
        def config = new MeasurementConfiguration(source: new URL("http://localhost"))
        def valid = config.validate()
        config.errors.allErrors.each {
            println it
        }
        then:
        valid
    }

    void "test validation 2"() {
        when:
        def config = new MeasurementConfiguration()
        def valid = config.validate()
        then:
        !valid
    }

    void "test validation 3"() {
        when:
        def config = new MeasurementConfiguration(format: 'xxx')
        def valid = config.validate()
        then:
        !valid
    }


}