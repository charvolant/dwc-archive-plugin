package au.org.ala.data.dwca.measurement

import au.org.ala.data.filter.ValueFilter
import au.org.ala.util.AlaTerm
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.FileItemHeaders
import org.gbif.dwc.terms.DwcTerm
import org.grails.databinding.SimpleMapDataBindingSource
import org.springframework.mock.web.MockMultipartFile
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

    void "test bind filter 1"() {
        when:
        def dataBinder = applicationContext.getBean('grailsWebDataBinder')
        def config = new MeasurementConfiguration(source: new URL("http://localhost"))
        SimpleMapDataBindingSource source = [filter: 'basisOfRecord == "Observation"']
        dataBinder.bind(config, source)
        then:
        config.filter != null
        config.filter.asExpression() == 'basisOfRecord == "Observation"'

    }

    void "test term file 1"() {
        when:
        def file = new MockMultipartFile("terms.json", this.class.getResourceAsStream("terms.json"))
        def config = new MeasurementConfiguration(source: new URL("http://localhost"), mappingFile: file)
        config.addMappingFile()
        then:
        config.terms.size() == 4
        config.terms[0].term == 'turbidityInNtus'
        config.filter == null
    }

    void "test term file 2"() {
        when:
        def file = new MockMultipartFile("terms2.json", this.class.getResourceAsStream("terms2.json"))
        def config = new MeasurementConfiguration(source: new URL("http://localhost"), mappingFile: file)
        config.addMappingFile()
        then:
        config.terms.size() == 4
        config.terms[0].term == 'turbidityInNtus'
        config.filter != null
        config.filter.class == ValueFilter.class
        config.filter.asExpression() == 'datasetName == "Bat Survey"'
    }

    void "test term file 3"() {
        when:
        def file = new MockMultipartFile("terms3.json", this.class.getResourceAsStream("terms3.json"))
        def config = new MeasurementConfiguration(source: new URL("http://localhost"), mappingFile: file)
        config.addMappingFile()
        then:
        config.terms.size() == 1
        config.terms[0].term == 'turbidityInNtus'
        config.values.size() == 2
        def val1 = config.values.find { it.term == DwcTerm.datasetID }
        val1 != null
        val1.value == '123'
        def val2 = config.values.find { it.term == DwcTerm.datasetName }
        val2 != null
        val2.value == 'Bat Survey'
    }

}