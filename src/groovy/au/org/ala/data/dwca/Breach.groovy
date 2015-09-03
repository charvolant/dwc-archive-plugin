package au.org.ala.data.dwca

import grails.converters.JSON
import grails.converters.XML
import org.gbif.dwc.text.ArchiveFile

/**
 * A breach of a set of constraints.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class Breach {
    /** The source of the breach */
    ArchiveFile source
    /** The specific breach code */
    String code
    /** The error message parameters */
    Object[] params

    /**
     * Is this a contstraint violation?
     *
     * @return True if this breach represents a hard violation of a rule
     */
    boolean isViolation() {
        return false
    }

    /**
     * Is this a caution?
     *
     * @return True if this breach represents a possible violation of a rule
     */
    boolean isCaution() {
        return false
    }

    /**
     * Convert into a map for marshalling
     *
     * @return The breach as a map suitable for JSON/XML encoding
     */
    def asMap() {
        return [
                source: source?.location,
                code: code,
                params: params?.collect { it.toString() }
        ]
    }

    // JSON/XML definition of the Breach object
    static {
        JSON.registerObjectMarshaller(Breach) { breach -> breach.asMap() }
        XML.registerObjectMarshaller(Breach) { breach -> breach.asMap() }
    }
}