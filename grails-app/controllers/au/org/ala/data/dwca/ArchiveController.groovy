package au.org.ala.data.dwca

import grails.converters.JSON
import grails.converters.XML

class ArchiveController {
    def imageArchiveService
    def messageSource

    /**
     * Get an index of actions
     */
    def index() {
        def configuration = new CheckConfiguration()
        render(view: "index", model: [configuration: configuration])
    }

    /**
     * Check an image archive
     *
     * @param configuration The check configuration
     */
    def checkImageArchive() {
        def configuration = new CheckConfiguration()
        bindData(configuration, params)
        if (configuration.hasErrors()) {
            flash.message = messageSource.getMessage("page.configuration.error", null, "Invalid parameters", null)
            withFormat {
                json { render(status: 400, text: (configuration.errors as JSON), contentType: "application/json")}
                xml { render(status: 400, text: (configuration.errors as XML), contentType: "text/xml") }
                '*' { render(status: 400, view: 'index', model: [configuration: configuration]) }
            }
        } else {
            def report = imageArchiveService.check(configuration)
            def model = [report: report, configuration: configuration]

            //log.warn "Parameters are ${params}"
            withFormat {
                json { render model as JSON }
                xml { render model as XML }
                '*' { render(view: "report", model: model) }
            }
        }
    }
}
