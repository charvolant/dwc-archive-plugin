package au.org.ala.data.dwca

import au.org.ala.data.dwca.measurement.MeasurementConfiguration
import grails.converters.JSON
import grails.converters.XML
import org.springframework.web.multipart.commons.CommonsMultipartFile

class ArchiveController {
    def imageArchiveService
    def measurementArchiveService
    def messageSource

    /**
     * Get an index of actions
     */
    def index() {
        render(view: "index")
    }

    /**
     * Validate a DwC archive
     */
    def validateArchive() {
        def configuration = new CheckConfiguration()
        render(view: "validate-archive", model: [configuration: configuration])
    }

    /**
     * Check an image archive
     *
     * @param configuration The check configuration
     */
    def checkArchive() {
        def configuration = new CheckConfiguration()
        bindData(configuration, params)
        if (!configuration.validate()) {
            flash.message = messageSource.getMessage("page.configuration.error", null, "Invalid parameters", null)
            withFormat {
                json { render(status: 400, text: (configuration.errors as JSON), contentType: "application/json") }
                xml { render(status: 400, text: (configuration.errors as XML), contentType: "text/xml") }
                '*' { render(status: 400, view: 'validate-archive', model: [configuration: configuration]) }
            }
        } else {
            def report = imageArchiveService.check(configuration)
            def model = [report: report, configuration: configuration]

            //log.warn "Parameters are ${params}"
            withFormat {
                json { render model as JSON }
                xml { render model as XML }
                '*' { render(view: "validate-archive-report", model: model) }
            }
        }
    }

    def flattenMeasurementArchive() {
        def configuration = new MeasurementConfiguration()
        render(view: "flatten-measurement-archive", model: [configuration: configuration])
    }

    def collectMeasurementTerms() {
        def configuration = new MeasurementConfiguration()
        bindData(configuration, params)
        if (!configuration.validate()) {
            flash.message = messageSource.getMessage("page.configuration.error", null, "Invalid parameters", null)
            withFormat {
                json { render(status: 400, text: (configuration.errors as JSON), contentType: "application/json") }
                xml { render(status: 400, text: (configuration.errors as XML), contentType: "text/xml") }
                '*' { render(status: 400, view: 'flatten-measurement-archive', model: [configuration: configuration]) }
            }
        } else {
            configuration = measurementArchiveService.collectTerms(configuration)
            def model = [configuration: configuration]
            withFormat {
                json { render model as JSON }
                xml { render model as XML }
                '*' { render(view: "flatten-measurement-archive-terms", model: model) }
            }
        }
    }

    def flattenMeasurementArchiveTerms() {
        def configuration = new MeasurementConfiguration()
        bindData(configuration, params)
        if (!configuration.validate()) {
            flash.message = messageSource.getMessage("page.configuration.error", null, "Invalid parameters", null)
            withFormat {
                json { render(status: 400, text: (configuration.errors as JSON), contentType: "application/json") }
                xml { render(status: 400, text: (configuration.errors as XML), contentType: "text/xml") }
                '*' {
                    render(status: 400, view: 'flatten-measurement-archive-terms', model: [configuration: configuration])
                }
            }
        } else {
            def archive = measurementArchiveService.pivot(configuration)

            log.debug "Archive content is: ${archive}"
            render(archive)
            archive.file.delete()
        }
    }

    def saveMeasurementArchiveTerms() {
        def configuration = new MeasurementConfiguration()
        bindData(configuration, params)
        if (!configuration.validate()) {
            flash.message = messageSource.getMessage("page.configuration.error", null, "Invalid parameters", null)
            withFormat {
                json { render(status: 400, text: (configuration.errors as JSON), contentType: "application/json") }
                xml { render(status: 400, text: (configuration.errors as XML), contentType: "text/xml") }
                '*' {
                    render(status: 400, view: 'flatten-measurement-archive-terms', model: [configuration: configuration])
                }
            }
        } else {
            def terms = configuration.terms.collect { term ->
                [term: term.simpleName(),
                 uri: term.qualifiedName(),
                 measurementType: term.measurementType,
                 measurementUnit: term.measurementUnit
                ]
            }
            def model = [terms: terms]
            if (configuration.filter)
                model['filter'] = configuration.filter.asExpression()
            def filename = configuration.rootFileName + ".json"
            response.setHeader("Content-disposition", "attachment; filename=" + filename)
            render model as JSON
        }
    }

    def flattenMeasurementArchiveComplete() {
        def configuration = new MeasurementConfiguration()
        bindData(configuration, params)
        if (!configuration.validate()) {
            withFormat {
                xml { render(status: 400, text: (configuration.errors as XML), contentType: "text/xml") }
                '*' { render(status: 400, text: (configuration.errors as JSON), contentType: "application/json") }
            }
        } else {
            configuration = measurementArchiveService.collectTerms(configuration)
            if (configuration.hasNewTerms && !configuration.allowNewTerms) {
                render(status: 400, text: "Unexpected terms were detected", contentType: "text/plain")

            } else {
                def archive = measurementArchiveService.pivot(configuration)
                log.debug "Archive content is: ${archive}"
                render(archive)
                archive.file.delete()
            }
        }

    }
}
