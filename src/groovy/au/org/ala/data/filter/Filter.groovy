package au.org.ala.data.filter

import grails.converters.JSON
import grails.converters.XML
import org.gbif.dwc.record.StarRecord

/**
 * A record filter.
 * <p>
 * Filters are composable tests that can be used to exmaine a record and see whether it should
 * be included in the final output set.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
abstract class Filter {
    /**
     * Test a record for validity.
     *
     * @param record The record (can be either a {@link org.gbif.dwc.record.StarRecord} or a (@link org.gbif.dwc.record.Record}
     *
     * @return True if the record passes the filter
     */
    abstract boolean filter(record);

    /**
     * Get the 'level' of the expression for parsing.
     * This allows us to dispense with brackets if needed.
     *
     * @return The level
     */
    abstract int getLevel()

    /**
     * Build a filter expression usable by the {@link FilterParser}
     *
     * @return The filter expression
     */
    String asExpression() {
        StringBuffer buffer = new StringBuffer(32)
        asExpression(buffer, 0)
        return buffer.toString()
    }

    /**
     * Add this expression to the bufffer
     *
     * @param buffer
     * @param level The current expression level (used to avoid excess parentheses)
     */
    abstract void asExpression(StringBuffer buffer, int level)
}
