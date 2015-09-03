package au.org.ala.data.dwca

import org.gbif.dwc.record.Record
import org.gbif.dwc.record.StarRecord
import org.gbif.dwc.terms.DcTerm
import org.gbif.dwc.terms.DwcTerm
import org.gbif.dwc.terms.GbifTerm
import org.gbif.dwc.terms.Term
import org.gbif.dwc.text.Archive
import org.gbif.dwc.text.ArchiveFactory
import org.gbif.dwc.text.ArchiveFile
import org.gbif.dwc.text.UnsupportedArchiveException
import org.gbif.utils.file.ClosableIterator

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class DwCArchiveChecker {
    // Check for a legit-looking URL
    static URL_PATTERN = ~/^(ftp|http)s?:\/\/[^\/]+\/.+/

    /** The source location */
    File location
    /** The source archive */
    Archive archive
    /** The error report */
    Report report
    /** The checker configuration */
    CheckConfiguration configuration

    DwCArchiveChecker(File location, CheckConfiguration configuration) {
        this.location = location
        this.report = new Report()
        this.configuration = configuration
    }

    /**
     * Check a DwCA archive
     */
    def check() {
        try {
            archive = ArchiveFactory.openArchive(location)
            checkArchive()
        } catch (FileNotFoundException ex) {
            report.addViolation(null, "archive.invalid", ex.getLocalizedMessage())
        } catch (UnsupportedArchiveException ex) {
            report.addViolation(null, "archive.invalid", ex.getLocalizedMessage())
        }
     }

    def checkArchive() {
        boolean hasImages = false

        if (!archive.metadataLocationFile)
            report.addCaution(null, "archive.metadata")
        checkCore(archive.getCore())
        if (configuration.checkImages) {
            hasImages = hasImages || checkImageExtension(archive.getExtension(GbifTerm.Image))
            hasImages = hasImages || checkImageExtension(archive.getExtension(GbifTerm.Multimedia))
            if (!hasImages)
                report.addViolation(null, "archive.image")
        }
        if (configuration.checkRecords)
            checkRecords()
    }

    def checkCore(ArchiveFile file) {
        try {
            if (file == null) {
                report.addViolation(null, "archive.core.invalid")
                return
            }
            if (!file.locationFile.exists())
                report.addViolation(file, "archive.file.missing")
            if (file.rowType != DwcTerm.Occurrence)
                report.addViolation(file, "archive.core.occurrence")
            if (file.id == null) {
                report.addViolation(file, "archive.core.id")
                configuration.checkRecords = false
            }
            if (configuration.checkUniqueTerms) {
                if (configuration.uniqueTerms.isEmpty()) {
                    report.addViolation(file, "archive.core.noUniqueTerms")
                    report.addCaution(file, "nocheck.uniqueTerm")
                    configuration.checkUniqueTerms = false
                }
                for (Term catalogTerm : configuration.uniqueTerms) {
                    if (!file.hasTerm(catalogTerm)) {
                        report.addCaution(file, "archive.core.uniqueTerm", catalogTerm.toString())
                        report.addCaution(file, "nocheck.uniqueTerm")
                        configuration.checkUniqueTerms = false
                    }
                }
            }
        } catch (Exception ex) {
            report.addViolation(file, "archive.core.error", ex.getLocalizedMessage())
        }
    }

    def checkImageExtension(ArchiveFile file) {
        try {
            if (file == null)
                return false
            if (!file.locationFile.exists())
                report.addViolation(file, "archive.file.missing")
            if (file.rowType == GbifTerm.Image)
                report.addCaution(file, "archive.image.imageRowType")
            if (file.id == null)
                report.addViolation(file, "archive.extension.coreId")
            if (!file.hasTerm(DcTerm.identifier)) {
                report.addViolation(file, "archive.image.identifier")
                report.addCaution(file, "nocheck.presence")
                configuration.checkPresence = false
            }
            if (!file.hasTerm(DcTerm.title))
                report.addCaution(file, "archive.image.title")
            if (!file.hasTerm(DcTerm.format))
                report.addCaution(file, "archive.image.format")
            if (!file.hasTerm(DcTerm.description))
                report.addCaution(file, "archive.image.description")
            if (!file.hasTerm(DcTerm.license))
                report.addCaution(file, "archive.image.license")
            if (!file.hasTerm(DcTerm.rights) && !file.hasTerm(DcTerm.rightsHolder))
                report.addCaution(file, "archive.image.rights")
        } catch (Exception ex) {
            report.addViolation(file, "archive.image.error", ex.getLocalizedMessage())
        }
        return true
    }

    def checkRecords() {
        ClosableIterator<StarRecord> records = archive.iterator()
        Set<String> uids = [] as Set<String>
        Set<String> images = [] as Set<String>

        try {
            for (StarRecord record : records) {
                def core = record.core()

                report.records++
                try {
                    if (!core.id() || core.id().isEmpty())
                        report.addViolation(archive.core, "archive.core.nullId")
                    if (configuration.checkUniqueTerms) {
                        def terms = configuration.uniqueTerms.collect { term -> core.value(term) }
                        String uid = terms.inject("") { String uid, val -> uid + (uid.isEmpty() ? "" : ", ") + val }
                        if (!uid || uid.isEmpty())
                            report.addViolation(archive.core, "archive.core.nullUid", core.id())
                        if (uids.contains(uid))
                            report.addViolation(archive.core, "archive.core.duplicateUid", uid, core.id())
                        else
                            uids.add(uid)
                    }
                    if (configuration.checkImages) {
                        def hasImage = checkImageRecord(record.extension(GbifTerm.Image), record, images)
                        hasImage = hasImage || checkImageRecord(record.extension(GbifTerm.Multimedia), record, images)
                        if (!hasImage)
                            report.addCaution(archive.core, "archive.core.noImages", core.id())
                    }
                } catch (Exception ex) {
                    report.addViolation(archive.core, "archive.core.recordError", core?.id() ?: "???", ex.getLocalizedMessage())
                }
            }
        } catch (Exception ex) {
        }
        records.close()
    }

    def checkImageRecord(List<Record> records, StarRecord source, Set<String> images) {
        def core = source.core()
        def hasImage = false
        for (Record record: records) {
            report.images++
            hasImage = true
            if (configuration.checkPresence) {
                String identifier = record.value(DcTerm.identifier)

                if (identifier == null || identifier.isEmpty()) {
                    report.addViolation(null, "archive.image.missingIdentifier", core.id())
                    continue
                } else if (images.contains(identifier)) {
                    report.addCaution(null, "archive.image.duplicateIdentifier", identifier, core.id())
                    continue
                } else
                    images.add(identifier)
                // Assumes URL valid
                if (URL_PATTERN.matcher(identifier).matches())
                    continue
                // Otherwise, see if it is present in the archive
                File imageFile = new File(location, identifier)
                if (!imageFile.exists())
                    report.addViolation(null, "archive.image.missingFile", identifier, source.core().id())

            }
        }
        return hasImage
    }

}
