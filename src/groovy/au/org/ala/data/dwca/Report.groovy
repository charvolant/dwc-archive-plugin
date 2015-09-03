package au.org.ala.data.dwca

import org.gbif.dwc.text.ArchiveFile

/**
 * A report on the state of a
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class Report {
    List<Breach> violations = []
    List<Caution> cautions = []
    int records = 0
    int images = 0

    /**
     * Does this report have any constraint violations
     *
     * @return True if a violation is found
     */
    boolean hasViolations() {
        !violations.isEmpty()
    }

    /**
     * Does this report have any constraint violations
     *
     * @return True if a violation is found
     */
    boolean hasCautions() {
        !cautions.isEmpty()
    }

    /**
     * Add a new violation to the list of errors.
     *
     * @param source The archive file that caused the violation
     * @param code The violation code
     * @param params Violation parameters
     *
     */
    def addViolation(ArchiveFile source, String code, Object... params) {
        violations.add(new Violation(code: code, params: params, source: source))
    }


    /**
     * Add a new caution to the list of errors.
     *
     * @param source The archive file that caused the violation
     * @param code The violation code
     * @param params Violation parameters
     *
     */
    def addCaution(ArchiveFile source, String code, Object... params) {
        cautions.add(new Caution(code: code, params: params, source: source))
    }
}
