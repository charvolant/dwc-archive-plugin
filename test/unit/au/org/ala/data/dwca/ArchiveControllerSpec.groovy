package au.org.ala.data.dwca

import au.org.ala.util.AlaTerm
import grails.test.mixin.TestFor
import org.gbif.dwc.terms.DwcTerm
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ArchiveController)
class ArchiveControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test index 1"() {
        when:
        controller.index()
        then:
        response.status == 200
        response.mimeType?.name == 'text/html'
        view == '/archive/index'
    }

    void "test validate archive 1"() {
        when:
        controller.validateArchive()
        then:
        response.status == 200
        response.contentType.startsWith('text/html')
        view == '/archive/validate-archive'
        model.containsKey('configuration')
        model.configuration.source.toString() == 'http://host/path/archive.zip'
        model.configuration.checkUniqueTerms == true
        model.configuration.uniqueTerms == [DwcTerm.catalogNumber ]
    }

    void "test check archive 1"() {
        when:
        controller.imageArchiveService = Mock(ImageArchiveService)
        controller.imageArchiveService.check(_) >> new Report()
        params.source = 'http://localhost/somewhere/archive.zip'
        controller.checkArchive()
        then:
        response.status == 200
        response.contentType.startsWith('text/html')
        view == '/archive/validate-archive-report'
        model.containsKey('configuration')
        model.configuration.source.toString() == 'http://localhost/somewhere/archive.zip'
        model.containsKey('report')
        !model.report.hasCautions()
        !model.report.hasViolations()
    }

    void "test check archive 2"() {
        when:
        controller.imageArchiveService = Mock(ImageArchiveService)
        controller.imageArchiveService.check(_) >> new Report()
        controller.response.format = 'json' // Documented request.contentType = JSON_CONTENT_TYPE doesn't work
        controller.checkArchive()
        then:
        response.status == 200
        response.contentType.startsWith('application/json')
        def json = response.json
        json != null
        json.configuration != null
        json.configuration.source == 'http://host/path/archive.zip'
        json.report != null
        json.report.violations == []
        json.report.cautions == []
    }

    void "test check archive 3"() {
        when:
        controller.imageArchiveService = Mock(ImageArchiveService)
        controller.imageArchiveService.check(_) >> new Report()
        params.source = '::I am an invalid URL'
        controller.checkArchive()
        then:
        response.status == 400
        response.contentType.startsWith('text/html')
        view == '/archive/validate-archive'
    }

    void "test flatten measurement archive 1"() {
        when:
        controller.flattenMeasurementArchive()
        then:
        response.status == 200
        response.contentType.startsWith('text/html')
        view == '/archive/flatten-measurement-archive'
        model.containsKey('configuration')
        model.configuration.source.toString() == 'http://host/path/archive.zip'
        model.configuration.terms == []
    }

    void "test collect measurement terms 1"() {
        when:
        def terms = [ new AlaTerm(term: 'fred') ]
        controller.measurementArchiveService = Mock(MeasurementArchiveService)
        controller.measurementArchiveService.collectTerms(_) >> terms
        params.source = 'http://localhost/somewhere/archive.zip'
        controller.collectMeasurementTerms()
        then:
        response.status == 200
        response.contentType.startsWith('text/html')
        view == '/archive/flatten-measurement-archive-terms'
        model.containsKey('configuration')
        model.configuration.source.toString() == 'http://localhost/somewhere/archive.zip'
        model.configuration.terms == terms
    }

    void "test collect measurement terms 2"() {
        when:
        params.source = ':: not a URL at all'
        controller.collectMeasurementTerms()
        then:
        response.status == 400
        response.contentType.startsWith('text/html')
        view == '/archive/flatten-measurement-archive'
    }

    void "test flatten measurement archive terms 1"() {
        when:
        params.source = 'http://localhost/somewhere/archive.zip'
        params['terms[0].term'] = 'fred'
        params['terms[0].measurementType'] = 'jim'
        def file = File.createTempFile("test", ".csv")
        controller.measurementArchiveService = Mock(MeasurementArchiveService)
        controller.measurementArchiveService.pivot(_) >> [ contentType: 'text/csv', file: file ]
        controller.flattenMeasurementArchiveTerms()
        then:
        response.status == 200
        response.contentType.startsWith('text/csv')
        response.contentLength == 0
        !file.exists()
     }

    void "test flatten measurement archive terms 2"() {
        when:
        params.source = ':: completely invalid'
        controller.flattenMeasurementArchiveTerms()
        then:
        response.status == 400
        response.contentType.startsWith('text/html')
        view == '/archive/flatten-measurement-archive-terms'
    }

}
