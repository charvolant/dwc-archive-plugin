package au.org.ala.util

import org.gbif.dwc.record.StarRecord
import org.gbif.dwc.terms.DwcTerm
import org.gbif.dwc.terms.Term
import org.gbif.dwc.text.ArchiveFactory
import spock.lang.Specification


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
@Mixin(ResourceExtractor)
class DwCAGeneratorSpec extends Specification {
    File working = null
    List<Term> terms
    List<Map<Term, String>> values
    DwCAGenerator generator
    File dwca = null

    def setup() {
        working = File.createTempFile("dwca-test", "")
        working.delete()
        working.mkdirs()
        terms = [DwcTerm.basisOfRecord, DwcTerm.catalogNumber, DwcTerm.eventDate ]
        values = [[(DwcTerm.basisOfRecord): 'HumanObservation', (DwcTerm.catalogNumber): 'C1', (DwcTerm.eventDate): '2015-10-12']]
    }

    def cleanup() {
        removeDir(working)
    }

    def "test generate dwca 1"() {
        when:
        def g = new DwCAGenerator<Map<Term, String>>(terms, working, { record, term -> record[term] })
        dwca = g.generate(values)
        then:
        dwca.exists()
        !g.archive.exists()
        def archive = ArchiveFactory.openArchive(dwca, working)
        archive.core.hasTerm(DwcTerm.occurrenceID)
        archive.core.hasTerm(DwcTerm.basisOfRecord)
        archive.core.hasTerm(DwcTerm.catalogNumber)
        archive.core.hasTerm(DwcTerm.eventDate)
        def i = archive.iterator()
        i.hasNext()
        def record = i.next()
        record.core().id() == '1'
        record.core().value(DwcTerm.occurrenceID) == '1'
        record.core().value(DwcTerm.basisOfRecord) == 'HumanObservation'
        record.core().value(DwcTerm.catalogNumber) == 'C1'
        record.core().value(DwcTerm.eventDate) == '2015-10-12'
        !i.hasNext()
    }

    def "test generate dwca 2"() {
        when:
        def g = new DwCAGenerator<Map<Term, String>>(terms, working, { record, term -> record[term] }, DwcTerm.Occurrence, DwcTerm.catalogNumber)
        dwca = g.generate(values)
        then:
        dwca.exists()
        !g.archive.exists()
        def archive = ArchiveFactory.openArchive(dwca, working)
        !archive.core.hasTerm(DwcTerm.occurrenceID)
        archive.core.hasTerm(DwcTerm.basisOfRecord)
        archive.core.hasTerm(DwcTerm.catalogNumber)
        archive.core.hasTerm(DwcTerm.eventDate)
        def i = archive.iterator()
        i.hasNext()
        def record = i.next()
        record.core().id() == 'C1'
        record.core().value(DwcTerm.basisOfRecord) == 'HumanObservation'
        record.core().value(DwcTerm.catalogNumber) == 'C1'
        record.core().value(DwcTerm.eventDate) == '2015-10-12'
        !i.hasNext()
    }

    def "test generate dwca 3"() {
        when:
        def g = new DwCAGenerator<Map<Term, String>>(terms, working, { record, term -> record[term] })
        g.begin()
        g.write(values)
        g.write(values)
        dwca = g.finish()
        g.cleanup()
        then:
        dwca.exists()
        !g.archive.exists()
        def archive = ArchiveFactory.openArchive(dwca, working)
        def i = archive.iterator()
        i.hasNext()
        def record1 = i.next()
        record1.core().id() == '1'
        record1.core().value(DwcTerm.occurrenceID) == '1'
        record1.core().value(DwcTerm.basisOfRecord) == 'HumanObservation'
        record1.core().value(DwcTerm.catalogNumber) == 'C1'
        record1.core().value(DwcTerm.eventDate) == '2015-10-12'
        i.hasNext()
        def record2 = i.next()
        record2.core().id() == '2'
        record2.core().value(DwcTerm.occurrenceID) == '2'
        record2.core().value(DwcTerm.basisOfRecord) == 'HumanObservation'
        record2.core().value(DwcTerm.catalogNumber) == 'C1'
        record2.core().value(DwcTerm.eventDate) == '2015-10-12'
        !i.hasNext()
    }

}