package au.org.ala.data.filter

import org.gbif.dwc.record.StarRecord

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class AndFilter extends Filter {
    /** The tests to combine */
    List<Filter> and

    @Override
    boolean filter(record) {
        return and.every { it.filter(record) }
    }

    @Override
    int getLevel() {
        return 2
    }

    @Override
    void asExpression(StringBuffer buffer, int level) {
        if (level > this.level) buffer.append('(')
        and.eachWithIndex { filter, index -> if (index > 0) buffer.append(" and "); filter.asExpression(buffer, this.level) }
        if (level > this.level) buffer.append(')')
    }
}
