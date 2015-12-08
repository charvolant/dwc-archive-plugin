package au.org.ala.data.filter

import org.gbif.dwc.record.Record
import org.gbif.dwc.record.StarRecord
import org.gbif.dwc.terms.Term

/**
 * Test a record for a specific value.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class ValueFilter extends Filter {
    /** The column */
    Term column
    /** The value */
    String value

    @Override
    int getLevel() {
        return 5
    }

    @Override
    boolean filter(Object record) {
        Record r
        String v

        if (!record)
            return false
        if (record instanceof StarRecord)
            r = ((StarRecord) record).core()
        else if (record instanceof Record)
            r = (Record) record
        else
            return false
        v = r.value(column)
        return v == value
    }


    @Override
    void asExpression(StringBuffer buffer, int level) {
        if (level > this.level) buffer.append('(')
        buffer.append(column.simpleName())
        buffer.append(' == "')
        buffer.append(value.replaceAll('"', '\\"'))
        buffer.append('"')
        if (level > this.level) buffer.append(')')
    }
}