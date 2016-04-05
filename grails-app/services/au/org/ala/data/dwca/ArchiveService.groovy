package au.org.ala.data.dwca

import au.org.ala.util.ResourceExtractor
import org.apache.commons.io.IOCase
import org.apache.commons.io.filefilter.SuffixFileFilter

/**
 * Generic Dawrin Core Archive services
 */
class ArchiveService extends ResourceExtractor {
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
     * @param fixBdrs Fix the bdrs escape error
     *
     * @return The return value of the action
     */
    def withDwCA(URL source, Closure action, Closure error = null, boolean fixBdrs = false) {
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
            if (fixBdrs)
                fixBdrsEscapeBug(unzippedFolderLocation)
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

    /**
     * Find potential files with the BDRS escape bug and fix any that might have it
     *
     * @param dir The directory to check
     */
    def fixBdrsEscapeBug(File dir) {
        for (File f: dir.listFiles((FileFilter) new SuffixFileFilter(".txt", IOCase.INSENSITIVE))) {
            log.info("Fixing BDRS escape bug in " + f)
            fixBdrsFile(f)
        }
    }

    /**
     * Fix a file with the BDRS escape bug.
     * <p>
     * Incorrectly quoted single quotes of the form <code>O"'Grady</code> becode <code>O''Grady</code>
     *
     * @param file The file to fix
     *
     */
    def fixBdrsFile(File file) {
        File renamed = File.createTempFile("bdrs", ".txt", file.parentFile)
        file.renameTo(renamed)
        int state = 0, ch
        def is = new FileInputStream(renamed)
        def os = new FileOutputStream(file)
        fixBdrsStream(is, os)
        is.close()
        os.close()
    }

    /**
     * Fix the BDRS bug in a stream of characters.
     *
     * @param is The input stream
     * @param os The output stream
     */
    def fixBdrsStream(InputStream is, OutputStream os) {
        int state = 0, ch

        while ((ch = is.read()) != -1) {
            switch (state) {
                case 0:
                    if (ch == '\'')
                        state = 1
                    os.write(ch)
                    break
                case 1:
                    if (ch == '"')
                        state = 3
                    else if (ch == '\'') {
                        state = 0
                        os.write(ch)
                    } else {
                        state = 2
                        os.write(ch)
                    }
                    break
                case 2:
                    if (ch == '"')
                        state = 3
                    else if (ch == '\'') {
                        os.write(ch)
                        state = 4
                    } else
                        os.write(ch)
                    break
                case 3:
                    os.write((int) (ch == '\'' ? '\'' : '"'))
                    os.write(ch)
                    state = 2
                    break;
                case 4:
                    state = ch == '\'' ? 2 : 0
                    os.write(ch)

            }
        }
        if (state == 3) // Hanging state
            os.write((int) '"')
    }

    /**
     * Clean the wotking directory of any files that have
     */
    def cleanup() {
        def workDir = new File(grailsApplication.config.workDir)
        def before = new Date(System.currentTimeMillis() - grailsApplication.config.temporaryFileLifetime)

        log.debug "Cleaning ${workDir} of files before ${before}"
        cleanDir(workDir, before, false, log)
    }
}
