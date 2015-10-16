package au.org.ala.util

import org.gbif.api.model.registry.Dataset
import org.gbif.dwc.terms.Term

/**
 * Generate a Darwin Core output for occurrence data.
 * <p>
 * The output is intended to be used for the sort of single-row output
 * that forms the basis of the ALA.
 * <p>
 * Subclasses can be used to generate the data either as
 * a DwC archive, as a simple CSV file or as something else.
 * <p>
 * The basic mechanism of the generator is
 *
 * <pre>
 *     def terms = [DwcTerm.occurrenceId, DwcTerm.catalogNumber, ...]
 *     def g = new Generator<Record>(terms, workdir, { record, term -> record.get(term) })
 *     try {
 *         g.begin()
 *         g.write(records1)
 *         g.write(records2)
 *         ...
 *         return g.finish()
 *     } finally {
 *         g.cleanup()
 *     }
 *</pre>
 *
 * A convienience method encapsulates this for a single source of records
 *
 * <pre>
 *     def g = new Generator<Record>(terms, workdir, { record, term -> record.get(term) })
 *     return g.generate(records1)
 * </pre>
 *
 * @param < R >  The record type
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
abstract class DwCGenerator<R> {
    /** The terms that make up the columns. The list order is also the column order */
    List<Term> terms
    /** The work directory */
    File working
    /** The mechanism for getting a term value. */
    Closure<String> getter

    /**
     * Construct a generator for a list of terms
     *
     * @param terms The term list
     * @param working The working directory for creating files
     * @param getter The value getter
     */
    def DwCGenerator(List<Term> terms, File working, Closure<String> getter) {
        this.terms = terms
        this.working = working
        this.getter = getter
    }

    /**
     * Provide metadata about the dataset, if available.
     * <p>
     * By default, this does nothing. Subclasses may have some way of handling this.
     *
     * @param eml The dataset description
     */
    void setEml(Dataset eml) {
    }

    /**
     * Write a collection of records.
     * <p>
     * This method goes though a complete cycle of file creation,
     * writing and cleanup.
     *
     * @param source The collection of records
     *
     * @return The resulting file
     */
    File generate(Iterable<R> source) {
        return generate(source.iterator())
    }

    /**
     * Write a collection of records.
     * <p>
     * This method goes though a complete cycle of file creation,
     * writing and cleanup.
     *
     * @param source The record iterator
     *
     * @return The resulting file
     */
    File generate(Iterator<R> source) {
        try {
            begin()
            write(source)
            return finish()
        } finally {
            cleanup()
        }
    }

    /**
     * Write a collection of records.
     * <p>
     * This method simply writes
     *
     * @param source The collection of records
     */
    void write(Iterator<R> source) {
        source.each { record ->
            List<String> values = terms.collect { term -> getter.call(record, term) }
            writeRow(values)
        }
    }

    /**
     * Start writing the data.
     * <p>
     * By default, this does nothing.
     * Subclasses can override this method to perform pre-generation checks and header generation.
     */
    void begin() {
    }

    /**
     * Write a complete row.
     *
     * @param values The values in the same order as {@link #terms}
     */
    abstract void writeRow(List<String> values)

    /**
     * Finish writing the data and package it into a single file.
     * <p>
     * This method can throw an exception if everything turns out to be too hard.
     * Use {@link #cleanup} for post generation clean-up.
     *
     * @return The generated file containing the DwC data
     */
    abstract File finish()

    /**
     * Clean-up after generating the data..
     * <p>
     * By default, this does nothing.
     * Subclasses can override this method to do things like remove temporary files after everything is
     * done and dusted.
     * This method should clean up, no matter what.
     */
    void cleanup() {
    }

    /**
     * Return the mime type of the content being returned
     *
     * @return The generators mime type
     */
    abstract String getMimeType()
}
