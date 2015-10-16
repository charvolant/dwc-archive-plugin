package au.org.ala.data.dwca

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(MeasurementArchiveService)
class MeasurementArchiveServiceSpec extends Specification {
    MeasurementArchiveService service

    def setup() {
        grailsApplication.config.workDir = System.getProperty("user.dir")
        service = new MeasurementArchiveService()
        service.archiveService = new ArchiveService()
        service.archiveService.grailsApplication = grailsApplication
    }


    def cleanup() {
    }

    void "test collect terms 1"() {
        when:
        def terms = service.collectTerms(this.class.getResource("bdrs1.zip"))
        then:
        terms.size() == 1
        terms[0].simpleName() == "availablePhosphateInMilligramPerLitre"
    }

    void "test collect terms 2"() {
        when:
        def terms = service.collectTerms(this.class.getResource("bdrs2.zip"))
        then:
        terms.size() == 4
        terms[0].simpleName() == "didYouCalibrateYourEcMeter"
        terms[1].simpleName() == "dissolvedOxygenInMilligramPerLitre"
        terms[2].simpleName() == "samplerGroupName"
        terms[3].simpleName() == "turbidityInNtus"
    }
}
