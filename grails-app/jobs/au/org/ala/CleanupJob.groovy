package au.org.ala

/**
 * A scheduled job to clean up any gunk left over from processing.
 */
class CleanupJob {
    def archiveService

    static triggers = {
      simple repeatInterval: (10 * 60 * 1000l) // execute job once in 10 minutes
    }

    def execute() {
        log.info "Cleanup archive junk"
        archiveService.cleanup()
    }
}
