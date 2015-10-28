package au.org.ala.data.dwca.measurement

import au.org.ala.util.AlaTerm
import spock.lang.Specification

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class MeasurementConfigurationSpec extends Specification {
    def setup() {
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

}