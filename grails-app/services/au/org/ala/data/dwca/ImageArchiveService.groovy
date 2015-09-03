package au.org.ala.data.dwca

import au.org.ala.util.ResourceExtractor
import grails.transaction.Transactional

@Transactional
@Mixin(ResourceExtractor)
class ImageArchiveService {
    def grailsApplication
    /**
     * Check a DwCA archive unzipped into a location
     *
     * @param unzippedFolderLocation The unzipped archive
     *
     * @return The report on the archive
     */
    def check(CheckConfiguration command) {
        log.debug("Check file at ${command.source}")
        def workDir = new File(grailsApplication.config.workDir)
        workDir = File.createTempFile("image-check", "", workDir)
        try {
            workDir.delete()
            workDir.mkdirs()
            log.debug("Work file for ${command.source} is ${workDir}")
            def zipFile = new File(workDir, "archive.zip")
            extractResource(command.source, zipFile)
            def unzippedFolderLocation = new File(workDir, "unzipped")
            unzippedFolderLocation.mkdirs()
            unzip(zipFile, unzippedFolderLocation, false)
            def checker = new DwCArchiveChecker(unzippedFolderLocation, command)

            checker.check()
            return checker.report
        } catch (IOException ex) {
            return reportException(ex)
        } finally {
            removeDir(workDir)
            log.debug("Cleaned up ${workDir}")
        }
    }

    /**
     * Generate an violation from an exception
     *
     * @param ex The source exception
     *
     * @return A report about the excepiton
     */
    def reportException(Exception ex) {
        def report = new Report()

        report.addViolation(null, "archive.source", ex.getLocalizedMessage())
        return report
    }
}
