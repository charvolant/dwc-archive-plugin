package au.org.ala.data.dwca

import au.org.ala.util.ResourceExtractor
import grails.transaction.Transactional
import org.gbif.dwc.terms.Term
import org.gbif.dwc.text.Archive
import org.gbif.dwc.text.ArchiveFactory
import org.gbif.dwc.text.DwcaWriter

/**
 * Generic Dawrin Core Archive services
 */
@Mixin(ResourceExtractor)
class ArchiveService {
    def grailsApplication

    /**
     * Perform some action on a DwCA.
     * <p>
     * The source DwCA is extracted into a work area, whatever is specified by the action
     * is performed and then the work area is cleaned up.
     * <p>
     * The action closure takes a single parameter, a {@link File} with the directory
     * the unpacked DwCA lives in.
     * <p>
     * The error closure takes a single parameter, the {@link Exception} that caused the error.
     *
     * @param source The source of the DwCA
     * @param action The action to perform
     * @param error An optional error action. If null, then any exceptions are propagated
     *
     * @return The return value of the action
     */
    def withDwCA(URL source, Closure action, Closure error = null) {
        def workDir = new File(grailsApplication.config.workDir)
        workDir = File.createTempFile("dwca", "", workDir)
        try {
            workDir.delete()
            workDir.mkdirs()
            log.debug("Work file for ${source} is ${workDir}")
            def zipFile = new File(workDir, "archive.zip")
            extractResource(source, zipFile)
            def unzippedFolderLocation = new File(workDir, "unzipped")
            unzippedFolderLocation.mkdirs()
            unzip(zipFile, unzippedFolderLocation, false)
            return action.call(unzippedFolderLocation)
        } catch (Exception ex) {
            if (error)
                return error.call(ex)
            throw ex
        } finally {
            removeDir(workDir)
            log.debug("Cleaned up ${workDir}")
        }
    }
}
