package au.org.ala.data.dwca.measurement

import au.org.ala.util.AlaTerm
import grails.validation.Validateable
import groovy.json.JsonSlurper
import org.apache.commons.io.FilenameUtils
import org.gbif.dwc.terms.Term
import org.springframework.web.multipart.commons.CommonsMultipartFile

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
@Validateable
class MeasurementConfiguration implements Cloneable {
    /** Currently supported formats */
    static VALID_FORMATS = [ 'csv', 'dwca' ]

    /** The source archive */
    URL source
    /** The source archive file name */
    CommonsMultipartFile sourceFile
    /** The mapping description file name */
    CommonsMultipartFile mappingFile
    /** The root file name for names */
    String rootFileName
    /** The archive format */
    String format = 'csv'
    /** The list of terms */
    List<AlaTerm> terms = []
    /** The configuration contains some additional new terms */
    boolean hasNewTerms


    static constraints = {
        source nullable: true, validator: { val, obj -> val != null || obj.sourceFile != null }
        sourceFile nullable: true, validator: { val, obj -> val != null || obj.source != null }
        format inList: VALID_FORMATS
    }

    /**
     * If there is a mapping file defined, add the terms in the mapping file to the term list.
     * <p>
     *     Existing terms take precedence over the mapping file.
     */
    void addMappingFile() {
        if (!mappingFile || mappingFile.size == 0)
            return
        JsonSlurper slurper = new JsonSlurper()
        def mapping = slurper.parse(mappingFile.inputStream.newReader())
        def map = termMap
        mapping.terms.each {
            if (!map.containsKey(it.measurementType)) {
                def term = new AlaTerm(term: it.term, uri: it.uri ? new URI(it.uri) : null, measurementType: it.measurementType, measurementUnit: it.measurementUnit)
                terms.add(term)
            }
        }
    }

    /**
     * Get a map of term measurementType entries to terms
     *
     * @return The measurement type -> term map
     */
    Map<String, AlaTerm> getTermMap() {
        return terms.inject([:], { map, term -> map[term.measurementType] = term; map })
    }

    /**
     * Terms sorted into {@link Term#simpleName} order
     */
    List<AlaTerm> getSortedTerms() {
        return terms.sort {t1, t2 -> t1.simpleName().compareTo(t2.simpleName()) }
    }

    /**
     * Clone, ensuring that there is a modifiable term list.
     */
    MeasurementConfiguration clone() {
        MeasurementConfiguration clone = (MeasurementConfiguration) super.clone()
        clone.terms = terms.clone()
        return clone
    }

    /**
     * Get the root of a filename for which
     * @return
     */
    String getRootFileName() {
        if (!rootFileName) {
            def base = (sourceFile ? sourceFile.originalFilename : source.file) ?: 'measurements'

            rootFileName = FilenameUtils.getBaseName(base)
        }
        return rootFileName
    }
}
