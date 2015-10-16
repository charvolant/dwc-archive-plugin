package au.org.ala.util

import org.apache.commons.logging.LogFactory
import org.gbif.api.model.registry.Dataset
import org.gbif.dwc.terms.DcTerm
import org.gbif.dwc.terms.DwcTerm
import org.gbif.dwc.terms.Term
import org.gbif.dwc.text.DwcaWriter

/**
 * Generate DwC data in the form of a Darwin Core Archive.
 * <p>
 * This uses the GBIF {@link DwcaWriter} which is a bit on the limited side and only generates tab-separated values.
 * A meta.xml file is generated, as is an eml.xml if one is available.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
@Mixin(ResourceExtractor)
class DwCAGenerator<R> extends DwCGenerator<R> {
    static log = LogFactory.getLog(DwCAGenerator.class)

    /** The row type term */
    Term rowType
    /** The row identifier term */
    Term rowid
    /** The GBIF writer */
    DwcaWriter writer
    /** The working directory to use when generating the archive */
    File archive
    /** The term that holds the row identifier */
    int idIndex = -1

    /**
     * Construct a dwca generator
     *
     * @param terms The list of terms to construct
     * @param working The working directory
     * @param getter The closure that is used to get a term from a supplied record
     * @param rowType An optional row Type (defaults to {@link DwcTerm#Occurrence}
     * @param rowid An optional term that uniquely identifies the row (defaults to the default rowid for the rowType)
     */
    def DwCAGenerator(List terms, File working, Closure getter, Term rowType = DwcTerm.Occurrence, Term rowid = null) {
        super(terms, working, getter)
        this.rowType = rowType
        this.rowid = rowid
    }

    @Override
    void setEml(Dataset eml) {
        writer.eml = eml
    }

    @Override
    void begin() {
        if (rowid == null) {
            // This matches the choices in DwcaWriter, since we can't access what it thinks it should be
            if(DwcTerm.Taxon == rowType) {
                rowid = DwcTerm.taxonID;
            } else if(DwcTerm.Occurrence == rowType) {
                rowid = DwcTerm.occurrenceID;
            } else if(DwcTerm.Identification == rowType) {
                rowid = DwcTerm.identificationID;
            } else if(DwcTerm.Event == rowType) {
                rowid = DwcTerm.eventID;
            } else {
                rowid = DcTerm.identifier;
            }
        }
        archive = File.createTempFile("dwca", "", working)
        archive.delete()
        archive.mkdirs()
        writer = new DwcaWriter(rowType, rowid, archive, false)
        idIndex = terms.indexOf(rowid)
    }

    @Override
    void cleanup() {
        try {
            removeDir(archive)
        } catch (Exception ex) {
            log.error("Unable to clean up DWCA creation", ex)
        }
    }

    @Override
    void writeRow(List<String> values) {
        def id = (idIndex != -1 ? values[idIndex] : null) ?: Long.toString(writer.recordsWritten + 1)
        writer.newRecord(id)
        for (int i = 0; i < terms.size(); i++) {
            if (i != idIndex)
                writer.addCoreColumn(terms[i], values[i])
        }
    }

    @Override
    File finish() {
        File dwca = File.createTempFile("dwca", ".zip", working)

        writer.close()
        zip(archive, dwca)
        return dwca
    }

    @Override
    String getMimeType() {
        return "application/zip"
    }
}
