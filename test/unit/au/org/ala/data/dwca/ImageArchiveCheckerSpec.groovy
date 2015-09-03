package au.org.ala.data.dwca

import au.org.ala.util.ResourceExtractor
import spock.lang.Specification


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
@Mixin(ResourceExtractor)
class ImageArchiveCheckerSpec extends Specification {
    void "test url pattern 1"() {
        expect:
        DwCArchiveChecker.URL_PATTERN.matcher("http://localhost/x.jpg").matches()
        DwCArchiveChecker.URL_PATTERN.matcher("https://localhost/x.jpg").matches()
        DwCArchiveChecker.URL_PATTERN.matcher("ftp://localhost/x.jpg").matches()
        DwCArchiveChecker.URL_PATTERN.matcher("ftps://localhost/x.jpg").matches()
    }

    void "test url pattern 2"() {
        expect:
        DwCArchiveChecker.URL_PATTERN.matcher("http://somewhere.com/x.jpg").matches()
        DwCArchiveChecker.URL_PATTERN.matcher("http://somewhere.com/images?f=x.jpg").matches()
        DwCArchiveChecker.URL_PATTERN.matcher("http://somewhere.com/images/file/x").matches()
        DwCArchiveChecker.URL_PATTERN.matcher("http://somewhere.com/images/file?f=jpeg&loc=x").matches()
    }

    void "test url pattern 3"() {
        expect:
        !DwCArchiveChecker.URL_PATTERN.matcher("x.jpg").matches()
        !DwCArchiveChecker.URL_PATTERN.matcher("http:x.jpg").matches()
        !DwCArchiveChecker.URL_PATTERN.matcher("http:/somewhere/images/file/x").matches()
    }

    void "test invalid archive 1"() {
        when:
        File nowhere = new File("nowhere/in/particular")
        CheckConfiguration config = new CheckConfiguration()
        DwCArchiveChecker checker = new DwCArchiveChecker(nowhere, config)
        checker.check()
        Report report = checker.report
        then:
        report.hasViolations()
        !report.hasCautions()
        report.violations.size() == 1
        report.violations[0].code == "archive.invalid"
    }

    void "test invalid archive 2 - no meta.xml"() {
        when:
        File archive = unzipToTemp(this.class.getResource("iatest2.zip"), true)
        CheckConfiguration config = new CheckConfiguration()
        DwCArchiveChecker checker = new DwCArchiveChecker(archive, config)
        checker.check()
        Report report = checker.report
        then:
        report.hasViolations()
        !report.hasCautions()
        report.violations.size() == 1
        report.violations[0].code == "archive.invalid"
    }

    void "test invalid archive 3 - core id"() {
        when:
        File archive = unzipToTemp(this.class.getResource("iatest3.zip"), true)
        CheckConfiguration config = new CheckConfiguration()
        DwCArchiveChecker checker = new DwCArchiveChecker(archive, config)
        checker.check()
        Report report = checker.report
        then:
        report.hasViolations()
        !report.hasCautions()
        report.violations.size() == 1
        report.violations[0].code == "archive.core.id"
    }

    void "test invalid archive 4 - extension id"() {
        when:
        File archive = unzipToTemp(this.class.getResource("iatest4.zip"), true)
        CheckConfiguration config = new CheckConfiguration()
        DwCArchiveChecker checker = new DwCArchiveChecker(archive, config)
        checker.check()
        Report report = checker.report
        then:
        report.hasViolations()
        report.hasCautions()
        report.violations.size() == 1
        report.violations[0].code == "archive.image"
    }

    void "test invalid archive 5 - repeated uid"() {
        when:
        File archive = unzipToTemp(this.class.getResource("iatest5.zip"), true)
        CheckConfiguration config = new CheckConfiguration()
        DwCArchiveChecker checker = new DwCArchiveChecker(archive, config)
        checker.check()
        Report report = checker.report
        then:
        report.hasViolations()
        !report.hasCautions()
        report.violations.size() == 1
        report.violations[0].code == "archive.core.duplicateUid"
    }

    void "test invalid archive 6 - missing identifier"() {
        when:
        File archive = unzipToTemp(this.class.getResource("iatest6.zip"), true)
        CheckConfiguration config = new CheckConfiguration()
        DwCArchiveChecker checker = new DwCArchiveChecker(archive, config)
        checker.check()
        Report report = checker.report
        then:
        report.hasViolations()
        !report.hasCautions()
        report.violations.size() == 1
        report.violations[0].code == "archive.image.missingIdentifier"
    }

    void "test invalid archive 7 - missing image file"() {
        when:
        File archive = unzipToTemp(this.class.getResource("iatest7.zip"), true)
        CheckConfiguration config = new CheckConfiguration()
        DwCArchiveChecker checker = new DwCArchiveChecker(archive, config)
        checker.check()
        Report report = checker.report
        then:
        report.hasViolations()
        !report.hasCautions()
        report.violations.size() == 1
        report.violations[0].code == "archive.image.missingFile"
    }

    void "test valid archive 1 - minimal"() {
        when:
        File archive = unzipToTemp(this.class.getResource("vatest1.zip"), true)
        CheckConfiguration config = new CheckConfiguration()
        DwCArchiveChecker checker = new DwCArchiveChecker(archive, config)
        checker.check()
        Report report = checker.report
        then:
        !report.hasViolations()
        report.hasCautions()
        report.cautions.size() == 7
        report.cautions[0].code == "archive.metadata"
        report.cautions[1].code == "archive.image.imageRowType"
        report.cautions[2].code == "archive.image.title"
        report.cautions[3].code == "archive.image.format"
        report.cautions[4].code == "archive.image.description"
        report.cautions[5].code == "archive.image.license"
        report.cautions[6].code == "archive.image.rights"
    }

    void "test valid archive 2 - external images"() {
        when:
        File archive = unzipToTemp(this.class.getResource("vatest2.zip"), true)
        CheckConfiguration config = new CheckConfiguration()
        DwCArchiveChecker checker = new DwCArchiveChecker(archive, config)
        checker.check()
        Report report = checker.report
        then:
        !report.hasViolations()
        !report.hasCautions()
    }

    void "test valid archive 3 - images present"() {
        when:
        File archive = unzipToTemp(this.class.getResource("vatest3.zip"), true)
        CheckConfiguration config = new CheckConfiguration()
        DwCArchiveChecker checker = new DwCArchiveChecker(archive, config)
        checker.check()
        Report report = checker.report
        then:
        !report.hasViolations()
        !report.hasCautions()
    }

    void "test valid archive 4 - duplicate image identifier"() {
        when:
        File archive = unzipToTemp(this.class.getResource("vatest4.zip"), true)
        CheckConfiguration config = new CheckConfiguration()
        DwCArchiveChecker checker = new DwCArchiveChecker(archive, config)
        checker.check()
        Report report = checker.report
        then:
        !report.hasViolations()
        report.hasCautions()
        report.cautions.size() == 1
        report.cautions[0].code == "archive.image.duplicateIdentifier"
    }

}