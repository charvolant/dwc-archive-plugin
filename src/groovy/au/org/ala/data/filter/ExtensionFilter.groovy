package au.org.ala.data.filter

import org.gbif.dwc.record.StarRecord
import org.gbif.dwc.terms.Term

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class ExtensionFilter extends Filter {
    /** The extension row type */
    Term rowType
    /** The filter for the extension */
    Filter filter

    @Override
    boolean filter(record) {
        if (!record instanceof StarRecord)
            return false
        if (!record.hasExtension(rowType))
            return false
        return record.extension(rowType).any { row -> filter.filter(row) }
    }

    @Override
    int getLevel() {
        return 0
    }

    @Override
    void asExpression(StringBuffer buffer, int level) {
        if (level > this.level) buffer.append('(')
        buffer.append(rowType.simpleName())
        buffer.append(".")
        filter.asExpression(buffer, this.level)
        buffer.append("")
        if (level > this.level) buffer.append(')')
    }

}
