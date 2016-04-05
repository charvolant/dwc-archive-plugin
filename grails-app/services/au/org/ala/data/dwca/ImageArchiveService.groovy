package au.org.ala.data.dwca

import au.org.ala.util.ResourceExtractor
import grails.util.Mixin

@Mixin(ResourceExtractor)
class ImageArchiveService {
    def archiveService
    def grailsApplication

    /**
     * Check a DwCA archive unzipped into a location
     *
     * @param unzippedFolderLocation The unzipped archive
     *
     * @return The report on the archive
     */
    def check(CheckConfiguration command) {
        File workFile = null;
        log.debug("Check file at ${command.source}")
        try {
            if (command.sourceFile && command.sourceFile.size > 0 && !command.source) {
                def workDir = new File(grailsApplication.config.workDir)
                workFile = File.createTempFile("work", ".zip", workDir)
                command.sourceFile.transferTo(workFile)
                command.source = workFile.toURI().toURL()
            }
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

                },
                command.fixBdrs
         )
        } finally {
            if (workFile != null)
                workFile.delete();
        }
    }
}
