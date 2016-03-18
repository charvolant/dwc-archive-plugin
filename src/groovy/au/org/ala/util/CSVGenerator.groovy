package au.org.ala.util

import au.com.bytecode.opencsv.CSVWriter
import au.org.ala.data.filter.Filter
import org.gbif.dwc.terms.Term

/**
 * Generate a simple CSV file from that data supplied.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class CSVGenerator<R> extends DwCGenerator<R> {
    File csv
    CSVWriter writer

    def CSVGenerator(List<Term> terms, File working, Filter filter, Closure<String> getter) {
        super(terms, working, filter, getter)
    }

    @Override
    void begin() {
        csv = File.createTempFile("dwc", ".csv", working)
        writer = new CSVWriter(new FileWriter(csv), (char) ',', (char) '"', (char) '\\')
        writer.writeNext((String[]) terms.collect({ it.simpleName() }).toArray())
    }

    @Override
    void writeRow(List<String> values) {
        writer.writeNext((String[]) values.toArray())
    }

    @Override
    File finish() {
        writer.close()
        return csv
    }

    @Override
    String getMimeType() {
        return "text/csv"
    }

}
