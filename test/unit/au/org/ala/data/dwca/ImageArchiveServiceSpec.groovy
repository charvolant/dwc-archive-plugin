package au.org.ala.data.dwca

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.util.GrailsConfig
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ImageArchiveService)
@TestMixin(ServiceUnitTestMixin)
class ImageArchiveServiceSpec extends Specification {
    ImageArchiveService service

    def setup() {
        grailsApplication.config.workDir = System.getProperty("user.dir")
        service = new ImageArchiveService()
        service.archiveService = new ArchiveService()
        service.archiveService.grailsApplication = grailsApplication
    }

    def cleanup() {
    }

    void "test check 1"() {
        when:
        def config = new CheckConfiguration(source: this.class.getResource("vatest1.zip"))
        Report report = service.check(config)
        then:
        report.hasViolations() == false
        report.hasCautions() == true
    }

    void "test check 2"() {
        when:
        def config = new CheckConfiguration(source: this.class.getResource("vatest2.zip"))
        Report report = service.check(config)
        then:
        report.hasViolations() == false
        report.hasCautions() == false
    }
}
