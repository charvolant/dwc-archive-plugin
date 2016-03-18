package au.org.ala.data.filter

import org.gbif.dwc.record.StarRecord

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class OrFilter extends Filter {
    /** The tests to combine */
    List<Filter> or

    @Override
    boolean filter(record) {
        return or.any { it.filter(record) }
    }

    @Override
    int getLevel() {
        return 1
    }

    @Override
    void asExpression(StringBuffer buffer, int level) {
        if (level > this.level) buffer.append('(')
        or.eachWithIndex { filter, index -> if (index > 0) buffer.append(" or "); filter.asExpression(buffer, this.level) }
        if (level > this.level) buffer.append(')')
    }

}
