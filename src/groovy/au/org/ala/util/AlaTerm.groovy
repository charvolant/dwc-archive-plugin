package au.org.ala.util

import org.gbif.dwc.terms.DcTerm
import org.gbif.dwc.terms.DwcTerm
import org.gbif.dwc.terms.Term

import javax.measure.unit.Unit

/**
 * A term for use with the Ala, Contains all kinds of juicy extras.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class AlaTerm implements Term {
    static DEFAULT_NAMESPACE = "http://vocabulary.ala.org.au/"

    /** The term name */
    String term
    /** The full term URI */
    URI uri
    /** The source measurement type, if ther is one */
    String measurementType
    /** The source measurement unit, if there is one */
    String measurementUnit
    /** The term unit, if there is one */
    Unit<?> unit

    /**
     * Get a simple term.
     * <p>
     *     The {@link #DEFAULT_NAMESPACE} is used to construct a full URI
     *     Or, if there is a DwC or DC term that matches, use that for a URI
     *
     * @param term The term,
     */
    void setTerm(String term) {
        this.term = term
        def standard = null
        try {
            standard = DwcTerm.valueOf(term)
        } catch (IllegalArgumentException e) {
            try {
                standard = DcTerm.valueOf(term)
            } catch (IllegalArgumentException e1) {
            }
        }
        this.uri = standard ? new URI(standard.qualifiedName()) : new URI(DEFAULT_NAMESPACE + term)
    }

    /**
     * Set a fully qualified name.
     * <p>
     *     The {@link #term} becomes the last name in the path
     *
     * @param uri The URI
     */
    void setUri(URI uri) {
        this.term = term
        this.uri = uri
        this.term = uri.path
        this.term = this.term.substring(this.term.lastIndexOf('/') + 1)
    }

    /**
     * The simple name of the term
     */
    @Override
    String simpleName() {
        return this.term
    }

    /**
     * The fully qualified name of the term
     */
    @Override
    String qualifiedName() {
        return this.uri.toASCIIString()
    }
}
