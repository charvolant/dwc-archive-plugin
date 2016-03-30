package au.org.ala.data.dwca

import au.org.ala.data.dwca.measurement.MeasurementConfiguration
import au.org.ala.data.dwca.measurement.TermMaker
import au.org.ala.util.CSVGenerator
import au.org.ala.util.DwCAGenerator
import org.gbif.api.model.registry.Comment
import org.gbif.dwc.record.Record
import org.gbif.dwc.record.StarRecord
import org.gbif.dwc.terms.DwcTerm
import org.gbif.dwc.terms.Term
import org.gbif.dwc.text.Archive
import org.gbif.dwc.text.ArchiveFactory

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
     * Pull apart a DwCA and collect the terms
     *
     * @param configuration The configuration
     *
     * @return A new configuration with the terms
     */
    MeasurementConfiguration collectTerms(MeasurementConfiguration configuration) {
        File workFile = null

        try {
            if (configuration.sourceFile && configuration.sourceFile.size > 0 && !configuration.source) {
                def workDir = new File(grailsApplication.config.workDir)
                workFile = File.createTempFile("work", ".zip", workDir)
                configuration.sourceFile.transferTo(workFile)
                configuration.source = workFile.toURI().toURL()
            }
            return (MeasurementConfiguration) archiveService.withDwCA(configuration.source, this.&performCollectTerms.curry(configuration))
        } finally {
        }
    }

    private MeasurementConfiguration performCollectTerms(MeasurementConfiguration configuration, File dir) {
        Archive archive = ArchiveFactory.openArchive(dir)
        Map<String, Term> terms
        TermMaker maker = new TermMaker()

        configuration = configuration.clone()
        configuration.addMappingFile()
        terms = configuration.termMap
        archive.getExtension(DwcTerm.MeasurementOrFact).iterator().each { Record record ->
            String type = record.value(DwcTerm.measurementType)?.trim()
            String unit = record.value(DwcTerm.measurementUnit)?.trim()

            if (!terms.containsKey(type)) {
                Term term = maker.convertTypeToTerm(type, unit)
                terms[type] = term
                configuration.newTerms << term
            }
        }
        configuration.terms = terms.values() as List
        return configuration
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
        def generator
        def workDir = new File(grailsApplication.config.workDir)
        def termMap = configuration.termMap
        def coreTerms = archive.core.fieldsSorted.collect { field -> field.term }
        def measurementTerms = termMap.values() as List
        measurementTerms = measurementTerms.sort { a, b -> a.simpleName() <=> b.simpleName() }
        def valueMap = configuration.valueMap
        def idTerm = archive.core.fieldsSorted[archive.core.id.index].term // You would expect archive.core.id.term to work, but noooo ...
        def valueTerms = valueMap.keySet() as List
        valueTerms = valueTerms.sort { a, b -> a.simpleName() <=> b.simpleName() }
        def allTerms = (coreTerms + measurementTerms + valueTerms).unique { term -> term?.qualifiedName() ?: 'unknown' }
        def extractValue = { StarRecord record, Term term ->
            String value = null
            if (valueMap.containsKey(term))
                value = valueMap[term]
            else {
                if (coreTerms.contains(term))
                    value = record.core().value(term)
                def measurements = record.extension(DwcTerm.MeasurementOrFact)
                def values = measurements?.findAll { ext -> termMap[ext.value(DwcTerm.measurementType)]?.qualifiedName() == term.qualifiedName() } ?: []
                values = values.collect { r -> r.value(DwcTerm.measurementValue) }
                value = values.inject(value, { val, v -> val ? val + configuration.valueSeparator + v : v })
            }
            value
        }
        if (configuration.format == 'dwca')
            generator = new DwCAGenerator<StarRecord>(allTerms, workDir, configuration.filter, extractValue, archive.core.rowType, idTerm)
        else
            generator = new CSVGenerator<StarRecord>(allTerms, workDir, configuration.filter, extractValue)
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

}
