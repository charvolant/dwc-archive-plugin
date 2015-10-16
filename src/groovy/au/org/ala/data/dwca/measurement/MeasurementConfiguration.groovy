package au.org.ala.data.dwca.measurement

import au.org.ala.util.AlaTerm
import grails.validation.Validateable
import org.gbif.dwc.terms.DwcTerm
import org.gbif.dwc.terms.Term

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
@Validateable
class MeasurementConfiguration {
    /** The source archive */
    URL source = new URL("http://host/path/archive.zip")
    /** The list of terms */
    List<AlaTerm> terms = []


    static constraints = {
    }

    /**
     * Get a map of term measurementType entries to terms
     *
     * @return The measurement type -> term map
     */
    Map<String, AlaTerm> getTermMap() {
        return terms.inject([:], { map, term -> map[term.measurementType] = term; map })
    }
}
