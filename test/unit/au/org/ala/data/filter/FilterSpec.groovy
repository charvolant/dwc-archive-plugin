package au.org.ala.data.filter

import au.org.ala.util.ResourceExtractor
import grails.util.Mixin
import org.gbif.dwc.record.StarRecord
import org.gbif.dwc.terms.DcTerm
import org.gbif.dwc.terms.DwcTerm
import org.gbif.dwc.terms.GbifTerm
import org.gbif.dwc.text.Archive
import org.gbif.dwc.text.ArchiveFactory
import spock.lang.Specification

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
@Mixin(ResourceExtractor)
class FilterSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test as expression 1"() {
        when:
        def filter = new ValueFilter(column: DwcTerm.basisOfRecord, value: 'fred')
        then:
        filter.asExpression() == 'basisOfRecord == "fred"'
    }

    void "test as expression 2"() {
        when:
        def filter1 = new ValueFilter(column: DwcTerm.basisOfRecord, value: 'fred')
        def filter2 = new ValueFilter(column: DwcTerm.collectionID, value: '23')
        def filter = new AndFilter(and: [filter1, filter2])
        then:
        filter.asExpression() == 'basisOfRecord == "fred" and collectionID == "23"'
    }

    void "test as expression 3"() {
        when:
        def filter1 = new ValueFilter(column: DwcTerm.basisOfRecord, value: 'fred')
        def filter2 = new ValueFilter(column: DwcTerm.collectionID, value: '23')
        def filter = new OrFilter(or: [filter1, filter2])
        then:
        filter.asExpression() == 'basisOfRecord == "fred" or collectionID == "23"'
    }

    void "test as expression 4"() {
        when:
        def filter1 = new ValueFilter(column: DwcTerm.basisOfRecord, value: 'fred')
        def filter = new NotFilter(not: filter1)
        then:
        filter.asExpression() == 'not basisOfRecord == "fred"'
    }

    void "test as expression 5"() {
        when:
        def filter1 = new ValueFilter(column: DwcTerm.basisOfRecord, value: 'fred')
        def filter2 = new ValueFilter(column: DwcTerm.collectionID, value: '23')
        def filter3 = new ValueFilter(column: DwcTerm.eventID, value: 'T45')
        def filter = new OrFilter(or: [new AndFilter(and: [filter1, filter2]), filter3])
        then:
        filter.asExpression() == 'basisOfRecord == "fred" and collectionID == "23" or eventID == "T45"'
    }

    void "test as expression 6"() {
        when:
        def filter1 = new ValueFilter(column: DwcTerm.basisOfRecord, value: 'fred')
        def filter2 = new ValueFilter(column: DwcTerm.collectionID, value: '23')
        def filter3 = new ValueFilter(column: DwcTerm.eventID, value: 'T45')
        def filter = new AndFilter(and: [new OrFilter(or: [filter1, filter2]), filter3])
        then:
        filter.asExpression() == '(basisOfRecord == "fred" or collectionID == "23") and eventID == "T45"'
    }

    void "test as expression 7"() {
        when:
        def filter1 = new ValueFilter(column: DwcTerm.basisOfRecord, value: 'fred')
        def filter2 = new ValueFilter(column: DwcTerm.collectionID, value: '23')
        def filter = new NotFilter(not: new OrFilter(or: [filter1, filter2]))
        then:
        filter.asExpression() == 'not (basisOfRecord == "fred" or collectionID == "23")'
    }

    void "test as expression 8"() {
        when:
        def filter1 = new ValueFilter(column: DwcTerm.basisOfRecord, value: 'fred')
        def filter2 = new ValueFilter(column: DwcTerm.collectionID, value: '23')
        def filter3 = new NotFilter(not: new ValueFilter(column: DwcTerm.eventID, value: 'T45'))
        def filter = new AndFilter(and: [new OrFilter(or: [filter1, filter2]), filter3])
        then:
        filter.asExpression() == '(basisOfRecord == "fred" or collectionID == "23") and not eventID == "T45"'
    }

    void "test filter value 1"() {
        when:
        def filter = new ValueFilter(column: DwcTerm.catalogNumber, value: 'T1')
        File af = unzipToTemp(this.class.getResource("../dwca/vatest1.zip"), true)
        Archive archive = ArchiveFactory.openArchive(af)
        StarRecord record = archive.iterator().next()
        then:
        filter.filter(record) == true
    }

    void "test filter value 2"() {
        when:
        def filter = new ValueFilter(column: DwcTerm.catalogNumber, value: 'T2')
        File af = unzipToTemp(this.class.getResource("../dwca/vatest1.zip"), true)
        Archive archive = ArchiveFactory.openArchive(af)
        StarRecord record = archive.iterator().next()
        then:
        filter.filter(record) == false
    }

    void "test filter extension 1"() {
        when:
        def filter = new ExtensionFilter(rowType: GbifTerm.Image, filter: new ValueFilter(column: DcTerm.identifier, value: 'http://localhost/T1.jpg'))
        File af = unzipToTemp(this.class.getResource("../dwca/vatest1.zip"), true)
        Archive archive = ArchiveFactory.openArchive(af)
        StarRecord record = archive.iterator().next()
        then:
        filter.filter(record) == true
    }

    void "test filter extension 2"() {
        when:
        def filter = new ExtensionFilter(rowType: GbifTerm.Image, filter: new ValueFilter(column: DcTerm.identifier, value: 'http://localhost/T2.jpg'))
        File af = unzipToTemp(this.class.getResource("../dwca/vatest1.zip"), true)
        Archive archive = ArchiveFactory.openArchive(af)
        StarRecord record = archive.iterator().next()
        then:
        filter.filter(record) == false
    }

    void "test filter extension 3"() {
        when:
        def filter = new ExtensionFilter(rowType: GbifTerm.Multimedia, filter: new ValueFilter(column: DcTerm.identifier, value: 'http://localhost/T1.jpg'))
        File af = unzipToTemp(this.class.getResource("../dwca/vatest1.zip"), true)
        Archive archive = ArchiveFactory.openArchive(af)
        StarRecord record = archive.iterator().next()
        then:
        filter.filter(record) == false
    }

    void "test filter extension 4"() {
        when:
        def filter = new ExtensionFilter(rowType: GbifTerm.Multimedia, filter: new ValueFilter(column: DcTerm.identifier, value: 'http://localhost/T1.jpg'))
        File af = unzipToTemp(this.class.getResource("../dwca/vatest2.zip"), true)
        Archive archive = ArchiveFactory.openArchive(af)
        StarRecord record = archive.iterator().next()
        then:
        filter.filter(record) == true
    }

    void "test filter extension 5"() {
        when:
        def filter = new ExtensionFilter(rowType: GbifTerm.Multimedia, filter: new ValueFilter(column: DcTerm.identifier, value: 'http://localhost/T2.jpg'))
        File af = unzipToTemp(this.class.getResource("../dwca/vatest2.zip"), true)
        Archive archive = ArchiveFactory.openArchive(af)
        StarRecord record = archive.iterator().next()
        then:
        filter.filter(record) == true
    }

    void "test filter and 1"() {
        when:
        def filter = new AndFilter(and: [new ValueFilter(column: DwcTerm.catalogNumber, value: '233550'), new ValueFilter(column: DwcTerm.basisOfRecord, value: 'HumanObservation')])
        File af = unzipToTemp(this.class.getResource("../dwca/bdrs1.zip"), true)
        Archive archive = ArchiveFactory.openArchive(af)
        StarRecord record = archive.iterator().next()
        then:
        filter.filter(record) == true
    }

    void "test filter and 2"() {
        when:
        def filter = new AndFilter(and: [new ValueFilter(column: DwcTerm.catalogNumber, value: '233550'), new ValueFilter(column: DwcTerm.basisOfRecord, value: 'MachineObservation')])
        File af = unzipToTemp(this.class.getResource("../dwca/bdrs1.zip"), true)
        Archive archive = ArchiveFactory.openArchive(af)
        StarRecord record = archive.iterator().next()
        then:
        filter.filter(record) == false
    }

    void "test filter or 1"() {
        when:
        def filter = new OrFilter(or: [new ValueFilter(column: DwcTerm.catalogNumber, value: '233553'), new ValueFilter(column: DwcTerm.catalogNumber, value: '233550')])
        File af = unzipToTemp(this.class.getResource("../dwca/bdrs1.zip"), true)
        Archive archive = ArchiveFactory.openArchive(af)
        StarRecord record = archive.iterator().next()
        then:
        filter.filter(record) == true
    }

    void "test filter or 2"() {
        when:
        def filter = new OrFilter(or: [new ValueFilter(column: DwcTerm.catalogNumber, value: '233551'), new ValueFilter(column: DwcTerm.catalogNumber, value: '233553')])
        File af = unzipToTemp(this.class.getResource("../dwca/bdrs1.zip"), true)
        Archive archive = ArchiveFactory.openArchive(af)
        StarRecord record = archive.iterator().next()
        then:
        filter.filter(record) == false
    }

    void "test filter not 1"() {
        when:
        def filter = new NotFilter(not: new ValueFilter(column: DwcTerm.catalogNumber, value: '233553'))
        File af = unzipToTemp(this.class.getResource("../dwca/bdrs1.zip"), true)
        Archive archive = ArchiveFactory.openArchive(af)
        StarRecord record = archive.iterator().next()
        then:
        filter.filter(record) == true
    }

    void "test filter not 2"() {
        when:
        def filter = new NotFilter(not: new ValueFilter(column: DwcTerm.catalogNumber, value: '233550'))
        File af = unzipToTemp(this.class.getResource("../dwca/bdrs1.zip"), true)
        Archive archive = ArchiveFactory.openArchive(af)
        StarRecord record = archive.iterator().next()
        then:
        filter.filter(record) == false
    }

}