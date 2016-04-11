package au.org.ala.data.dwca

import au.org.ala.util.ResourceExtractor
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import org.gbif.dwc.terms.DwcTerm
import org.gbif.dwc.text.Archive
import org.gbif.dwc.text.ArchiveFactory
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ArchiveService)
@TestMixin(ServiceUnitTestMixin)
class ArchiveServiceSpec extends Specification {
    ArchiveService archiveService
    File work
    def setup() {
        work = File.createTempFile("test", "")
        work.delete()
        work.mkdirs()
        grailsApplication.config.workDir = work.getCanonicalPath()
        grailsApplication.config.connectTimeout = 10000
        grailsApplication.config.readTimeout = 10000
        archiveService = new ArchiveService()
        archiveService.grailsApplication = grailsApplication
    }

    def cleanup() {
        def re = new ResourceExtractor()
        re.removeDir(work)
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
        message == "Invalid archive: Cannot invoke method openConnection() on null object"
    }

    void "test with dwca 3"() {
        when:
        def message = archiveService.withDwCA(new URL('file:///nothing')) { location -> "Created archive!" } { ex -> "Invalid archive: ${ex.message}" }
        then:
        message == "Invalid archive: /nothing (No such file or directory)"
    }

    void "test clean up 1"() {
        when:
        def file = File.createTempFile("test", ".txt", work)
        grailsApplication.config.temporaryFileLifetime = 10000L
        archiveService.cleanup()
        then:
        file.exists() == true
    }

    void "test clean up 2"() {
        when:
        def file = File.createTempFile("test", ".txt", work)
        Thread.sleep(20L)
        grailsApplication.config.temporaryFileLifetime = 10L
        archiveService.cleanup()
        then:
        file.exists() == false
    }

    void "test fixBdrsStream 1"() {
        when:
        def is = new ByteArrayInputStream("Hello, there".bytes)
        def os = new ByteArrayOutputStream(32)
        archiveService.fixBdrsStream(is, os)
        then:
        def result = os.toString()
        result == "Hello, there"
    }

    void "test fixBdrsStream 2"() {
        when:
        def is = new ByteArrayInputStream("'Enclosed', 'quotes'".bytes)
        def os = new ByteArrayOutputStream(32)
        archiveService.fixBdrsStream(is, os)
        then:
        def result = os.toString()
        result == "'Enclosed', 'quotes'"
    }

    void "test fixBdrsStream 3"() {
        when:
        def is = new ByteArrayInputStream("'O''Grady'".bytes)
        def os = new ByteArrayOutputStream(32)
        archiveService.fixBdrsStream(is, os)
        then:
        def result = os.toString()
        result == "'O''Grady'"
    }

    void "test fixBdrsStream 4"() {
        when:
        def is = new ByteArrayInputStream("'O\"'Grady'".bytes)
        def os = new ByteArrayOutputStream(32)
        archiveService.fixBdrsStream(is, os)
        then:
        def result = os.toString()
        result == "'O''Grady'"
    }

    void "test fixBdrsStream 5"() {
        when:
        def is = new ByteArrayInputStream("'Something \"quoted\" '".bytes)
        def os = new ByteArrayOutputStream(32)
        archiveService.fixBdrsStream(is, os)
        then:
        def result = os.toString()
        result == "'Something \"quoted\" '"
    }

    void "test fixBdrsStream 6"() {
        when:
        def is = new ByteArrayInputStream("'Something \"quoted\"".bytes)
        def os = new ByteArrayOutputStream(32)
        archiveService.fixBdrsStream(is, os)
        then:
        def result = os.toString()
        result == "'Something \"quoted\""
    }

    void "test fixBdrsStream 7"() {
        when:
        def is = new ByteArrayInputStream("'',''".bytes)
        def os = new ByteArrayOutputStream(32)
        archiveService.fixBdrsStream(is, os)
        then:
        def result = os.toString()
        result == "'',''"
    }

    void "test fixBdrsDir 1"() {
        boolean coreOK1 = false
        boolean coreOK2 = false
        boolean extOK1 = false
        boolean extOK2 = false
        when:
        archiveService.withDwCA(this.class.getResource("bdrs4.zip"), { File location ->
            // Have to do this in here because the file will be cleaned up afterwards
            Archive archive = ArchiveFactory.openArchive(location)
            def i, r, v
            i = archive.core.iterator()
            r = i.next()
            v = r.value(DwcTerm.datasetName)
            coreOK1 = v == "O'Grady"
            r = i.next()
            v = r.value(DwcTerm.datasetName)
            coreOK2 = v == "O'Malley"
            i = archive.getExtension(DwcTerm.MeasurementOrFact).iterator()
            r = i.next()
            v = r.value(DwcTerm.measurementRemarks)
            extOK1 =  v == "survey: O'Grady"
            r = i.next()
            v = r.value(DwcTerm.measurementRemarks)
            extOK2 = v == "survey: O'Malley"
        },
        null, true)
        then:
        coreOK1
        coreOK2
        extOK1
        extOK2

    }
}
