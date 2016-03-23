package au.org.ala.data.dwca

import au.com.bytecode.opencsv.CSVReader
import au.org.ala.data.dwca.measurement.MeasurementConfiguration
import au.org.ala.util.AlaTerm
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(MeasurementArchiveService)
class MeasurementArchiveServiceSpec extends Specification {
    MeasurementArchiveService service
    def response = null

    def setup() {
        grailsApplication.config.workDir = System.getProperty("user.dir")
        service = new MeasurementArchiveService()
        service.grailsApplication = grailsApplication
        service.archiveService = new ArchiveService()
        service.archiveService.grailsApplication = grailsApplication
    }


    def cleanup() {
        if (response != null)
            response.file.delete()
    }

    void "test collect terms 1"() {
        when:
        def config = new MeasurementConfiguration(source: this.class.getResource("bdrs1.zip"))
        def result = service.collectTerms(config)
        def terms = result.terms
        then:
        terms.size() == 1
        terms[0].simpleName() == "availablePhosphateInMilligramPerLitre"
    }

    void "test collect terms 2"() {
        when:
        def config = new MeasurementConfiguration(source: this.class.getResource("bdrs2.zip"))
        def result = service.collectTerms(config)
        def terms = result.sortedTerms
        then:
        terms.size() == 4
        terms[0].simpleName() == "didYouCalibrateYourEcMeter"
        terms[1].simpleName() == "dissolvedOxygenInMilligramPerLitre"
        terms[2].simpleName() == "samplerGroupName"
        terms[3].simpleName() == "turbidityInNtus"
    }


    void "test pivot 1"() {
        when:
        def config = new MeasurementConfiguration(
                source: this.class.getResource("bdrs1.zip"),
                terms: [
                        new AlaTerm(term: 'availablePhosphateInMilligramPerLitre', measurementType: 'Available Phosphate (mg/L)')
                ]
        )
        response = service.pivot(config)
        then:
        response.file.exists()
        response.contentType == "text/csv"
    }

    void "test pivot 2"() {
        when:
        def config = new MeasurementConfiguration(
                source: this.class.getResource("bdrs1.zip"),
                format: 'dwca',
                terms: [
                        new AlaTerm(term: 'availablePhosphateInMilligramPerLitre', measurementType: 'Available Phosphate (mg/L)')
                ]
        )
        response = service.pivot(config)
        then:
        response.file.exists()
        response.contentType == "application/zip"
    }

    void "test pivot 3"() {
        when:
        def config = new MeasurementConfiguration(
                source: this.class.getResource("bdrs3.zip"),
                terms: [
                        new AlaTerm(term: 'x', measurementType: 'X1'),
                        new AlaTerm(term: 'x', measurementType: 'X2'),
                        new AlaTerm(term: 'a', measurementType: 'X3'),
                ]
        )
        response = service.pivot(config)
        then:
        response.file.exists()
        response.contentType == "text/csv"
        def reader = new CSVReader(new FileReader(response.file))
        def line1 = reader.readNext()
        line1[0] == 'catalogNumber'
        line1[1] == 'scientificName'
        line1[2] == 'a'
        line1[3] == 'x'
        def line2 = reader.readNext()
        line2[0] == '233553'
        line2[1] == 'Name'
        line2[2] == 'Something'
        line2[3] == 'Hello|Goodbye'
    }
}
