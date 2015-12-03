package au.org.ala.data.dwca

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

}
