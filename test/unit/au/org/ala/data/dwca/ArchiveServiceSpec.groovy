package au.org.ala.data.dwca

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ArchiveService)
@TestMixin(ServiceUnitTestMixin)
class ArchiveServiceSpec extends Specification {
    ArchiveService archiveService
    def setup() {
        grailsApplication.config.workDir = System.getProperty("user.dir")
        archiveService = new ArchiveService()
        archiveService.grailsApplication = grailsApplication
    }

    def cleanup() {
    }

    void "test with dwca 1"() {
        when:
        def files = archiveService.withDwCA(this.class.getResource("vatest1.zip")) { File location ->
            // Havwe to do this in here because the file will be cleaned up afterwards
            def meta = new File(location, "meta.xml")
            def occurrences = new File(location, "occurrences.csv")
            def images = new File(location, "images.csv")
            return [
                    [file : meta, valid: meta.exists()],
                    [file : occurrences, valid: occurrences.exists()],
                    [file : images, valid: images.exists()],
            ]
        }
        then:
        !files.any { !it.valid || it.file.exists() }
    }

    void "test with dwca 2"() {
        when:
        def message = archiveService.withDwCA(null) { location -> "Created archive!" } { ex -> "Invalid archive: ${ex.message}" }
        then:
        message == "Invalid archive: Cannot invoke method openStream() on null object"
    }

    void "test with dwca 3"() {
        when:
        def message = archiveService.withDwCA(new URL('file:///nothing')) { location -> "Created archive!" } { ex -> "Invalid archive: ${ex.message}" }
        then:
        message == "Invalid archive: /nothing (No such file or directory)"
    }
}
