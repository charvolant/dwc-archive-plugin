package au.org.ala.data.dwca

import au.org.ala.util.ResourceExtractor
import grails.transaction.Transactional
import grails.util.Mixin

@Mixin(ResourceExtractor)
class ImageArchiveService {
    def archiveService

    /**
     * Check a DwCA archive unzipped into a location
     *
     * @param unzippedFolderLocation The unzipped archive
     *
     * @return The report on the archive
     */
    def check(CheckConfiguration command) {
        log.debug("Check file at ${command.source}")
        archiveService.withDwCA(command.source,
                { File dir ->
                    def checker = new DwCArchiveChecker(dir, command)
                    checker.check()
                    return checker.report
                },
                { Exception ex ->
                    def report = new Report()
                    report.addViolation(null, "archive.source", ex.getLocalizedMessage())
                    return report

                })
    }
}
