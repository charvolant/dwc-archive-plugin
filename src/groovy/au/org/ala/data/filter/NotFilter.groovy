package au.org.ala.data.filter

import org.gbif.dwc.record.StarRecord

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class NotFilter extends Filter {
    /** The tests to combine */
    Filter not

    @Override
    boolean filter(record) {
        return ! not.filter(record)
    }


    @Override
    int getLevel() {
        return 3
    }


    @Override
    void asExpression(StringBuffer buffer, int level) {
        if (level > this.level) buffer.append('(')
        buffer.append("not ")
        not.asExpression(buffer, this.level)
        if (level > this.level) buffer.append(')')
    }

}
