package au.org.ala.data.dwca

import grails.validation.Validateable
import org.gbif.dwc.terms.DwcTerm
import org.gbif.dwc.terms.Term

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
@Validateable
class CheckConfiguration {
    /** The source to validate from */
    URL source = new URL("http://host/path/archive.zip")
    /** Check to see whether the archive has ALA-usable terms that can used to build a unique key */
    boolean checkUniqueTerms = true
    /** The list of terms used to build a unique key */
    List<Term> uniqueTerms = [ DwcTerm.catalogNumber ]
    /** Check individual records */
    boolean checkRecords = true
    /** Check image extensions */
    boolean checkImages = true
    /** Check to see whether the image files are present */
    boolean checkPresence = true


    static constraints = {
        source nullable: false
    }

    String getUniqueTermList() {
        uniqueTerms.collect({ term -> term.simpleName() }).join(", ")
    }

    void setUniqueTermList(String terms) {
        uniqueTerms = terms.split(",\\s*").collect { t -> DwcTerm.valueOf(t) }
    }
}
