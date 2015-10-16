package au.org.ala.data.dwca

import au.org.ala.data.dwca.measurement.MeasurementConfiguration
import au.org.ala.data.dwca.measurement.TermMaker
import au.org.ala.util.DwCAGenerator
import au.org.ala.util.CSVGenerator
import org.gbif.api.model.registry.Comment
import org.gbif.dwc.record.Record
import org.gbif.dwc.record.StarRecord
import org.gbif.dwc.terms.DwcTerm
import org.gbif.dwc.terms.Term
import org.gbif.dwc.text.Archive
import org.gbif.dwc.text.ArchiveFactory
import org.gbif.dwc.text.DwcaWriter

/**
 * A service that allows the deconstruction of an archive with a MeasurementOrFact
 * extension that can re-work the archive into a set of flat occurrence records
 * and its reconstruction into the flat structure that the biocahce can easily
 * manage.
 */
//@Transactional
class MeasurementArchiveService {
    def grailsApplication
    def archiveService
    def messageSource

    /**
     * Pull apart a DwCA and collect
     * @param source
     * @return
     */
    List<Term> collectTerms(URL source) {
        (List<Term>) archiveService.withDwCA(source, this.&performCollectTerms)
    }

    /**
     * Take a DwCA archive with a measurement extension and a list of measurement types mapped onto terms and
     * flatten the archive by mapping the measurement types onto new columns in the core
     *
     * @param configuration The source and terms
     *
     * @return A dictionary with a file: and a contentType: suitable for plugging into a
     */
    def pivot(MeasurementConfiguration configuration) {
        return archiveService.withDwCA(configuration.source, this.&performPivot.curry(configuration))
    }

    def performPivot(MeasurementConfiguration configuration, File dir) {
        Archive archive = ArchiveFactory.openArchive(dir)
        def workDir = new File(grailsApplication.config.workDir)
        def termMap = configuration.termMap
        def coreTerms = archive.core.fieldsSorted.collect { field -> field.term }
        def measurementTerms = termMap.keySet().collect { name -> termMap[name] }
        def idTerm = archive.core.fieldsSorted[archive.core.id.index].term // You would expect archive.core.id.term to work, but noooo ...

        measurementTerms = measurementTerms.unique { term -> term.qualifiedName() }
        measurementTerms = measurementTerms.sort { a, b -> a.simpleName() <=> b.simpleName() }
        /*
        def generator = new DwCAGenerator<StarRecord>(coreTerms + measurementTerms, workDir, { StarRecord record, Term term ->
            String value = null
            if (coreTerms.contains(term))
                value = record.core().value(term)
            else {
                def measurements = record.extension(DwcTerm.MeasurementOrFact)
                def measurement = measurements?.find { ext -> termMap[ext.value(DwcTerm.measurementType)] == term }

                value = measurement?.value(DwcTerm.measurementValue)
            }
            value
        }, archive.core.rowType, idTerm)
        */
        def generator = new CSVGenerator<StarRecord>(coreTerms + measurementTerms, workDir, { StarRecord record, Term term ->
            String value = null
            if (coreTerms.contains(term))
                value = record.core().value(term)
            else {
                def measurements = record.extension(DwcTerm.MeasurementOrFact)
                def measurement = measurements?.find { ext -> termMap[ext.value(DwcTerm.measurementType)] == term }

                value = measurement?.value(DwcTerm.measurementValue)
            }
            value
        })
        if (archive.metadataLocation) {
            // Copy metadata and add processing comment
            def eml = archive.getMetadata()
            def comment = new Comment()
            comment.created = new Date()
            comment.createdBy = messageSource.getMessage("measurement.comment.createdBy", null, null)
            comment.content = messageSource.getMessage("measurement.comment.comment", null, null)
            eml.comments.add(comment)
            generator.setEml(eml)
        }
        def file = generator.generate(archive.iterator())
        return [contentType: generator.mimeType, file: file]
    }

    private List<Term> performCollectTerms(File dir) {
        Archive archive = ArchiveFactory.openArchive(dir)
        Map<String, Term> terms = [:]
        TermMaker maker = new TermMaker()

        archive.getExtension(DwcTerm.MeasurementOrFact).iterator().each { Record record ->
            String type = record.value(DwcTerm.measurementType)
            String unit = record.value(DwcTerm.measurementUnit)

            if (!terms.containsKey(type)) {
                Term term = maker.convertTypeToTerm(type, unit)
                terms[type] = term
            }
        }
        def termList = terms.values() as List<Term>
        return termList.sort {t1, t2 -> t1.simpleName().compareTo(t2.simpleName()) }
    }


}
