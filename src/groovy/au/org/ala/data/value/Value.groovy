package au.org.ala.data.value

import grails.validation.Validateable
import org.gbif.dwc.terms.Term

/**
 * A specific value to set a term to.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 * Copyright (c) 2016 CSIRO
 *
 */
@Validateable
class Value {
    static constraints = {
        term empty: false
    }

    Term term;
    String value;

    Value(Term term, String value) {
        this.term = term
        this.value = value;
    }

    Value(String name, String value) {
        this.term = ValueParser.buildTerm(name)
        this.value = value;
    }

}
