package au.org.ala.data.dwca

import grails.validation.Validateable
import org.gbif.dwc.terms.DwcTerm
import org.gbif.dwc.terms.Term
import org.springframework.web.multipart.commons.CommonsMultipartFile

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
@Validateable
class CheckConfiguration {
    /** The source to validate from */
    URL source
    /** The source archive file name */
    CommonsMultipartFile sourceFile
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
        source nullable: true, validator: { val, obj -> val != null || obj.sourceFile != null }
        sourceFile nullable: true, validator: { val, obj -> val != null || obj.source != null }
    }

    String getUniqueTermList() {
        uniqueTerms.collect({ term -> term.simpleName() }).join(", ")
    }

    void setUniqueTermList(String terms) {
        uniqueTerms = terms.split(",\\s*").collect { t -> DwcTerm.valueOf(t) }
    }

    String getSourceName() {
        sourceFile && sourceFile.size > 0 ? sourceFile.originalFilename : source.toExternalForm()
    }
}
