package au.org.ala.data.dwca.measurement

import au.org.ala.util.AlaTerm
import au.org.ala.util.units.NameUnitFormat

import javax.measure.unit.BaseUnit
import javax.measure.unit.Unit
import javax.measure.unit.UnitFormat
import java.text.ParseException


/**
 * Convert some sort of long and complicated phrase into a suitable term
 * usable by Darwin Core.
 * <p>
 *     This class uses the UCAR unit database because it has unit names as well
 *     as symbols, unlike the jscience implementation of JSR 275. Unfortunately,
 *     it doesn't play nice with non plain ASCII characters and
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class TermMaker {
    /** The pattern for a measurement phrase with an optional unit attached */
    static PHRASE_PATTERN = ~/([^(]+)(\(.+\))?/
    static BASE_FORMAT = UnitFormat.instance
    static ASCII_FORMAT = UnitFormat.UCUMInstance
    static SPELL_FORMAT = NameUnitFormat.DEFAULT_NAME_FORMAT

    Map<String, Unit<?>> unitDictionary

    /**
     * Construct a term maker
     *
     * @param unitDictionary An optional dictionary for describing non-standard units
     */
    TermMaker(Map<String, Unit<?>> unitDictionary = [:]) {
        this.unitDictionary = unitDictionary
    }

    /**
     * Convert a measurement type string into a term usable by DwC.
     *
     * @param measurementType The {@link DwcTerm#measurementType}
     * @param measurementUnit The {@link DwcTerm#measurementUnit}
     *
     * @return The corresponding term
     */
    AlaTerm convertTypeToTerm(String measurementType, String measurementUnit) {
        def namepair = namePair(measurementType, measurementUnit)
        def unit = parseUnit(namepair.unit)
        def unitName = unit == null ? null : SPELL_FORMAT.format(unit)
        def name = camelCase(unitName == null ? namepair.quantity : namepair.quantity + " in " + unitName, false)
        def term = new AlaTerm()

        term.term = name
        term.unit = unit
        term.measurementType = measurementType
        term.measurementUnit = measurementUnit
        return term
    }

    /**
     * Convert a type into a name/unit pair
     *
     * @param measurementType The {@link DwcTerm#measurementType}
     * @param measurementUnit The {@link DwcTerm#measurementUnit}
     *
     */
    def namePair(String measurementType, String measurementUnit) {
        def quantity = null
        def unit = null

        if (measurementType == null || measurementType.isEmpty()) {
            quantity = "unknown"
        } else if (measurementUnit != null) {
            quantity = measurementType
            unit = measurementUnit
        } else {
            def match = PHRASE_PATTERN.matcher(measurementType)
            if (match.matches() && match.groupCount() == 2) {
                quantity = match.group(1).trim()
                unit = match.group(2)

                if (unit != null) { // Strip ()
                    unit = unit.substring(1, unit.length() - 1).trim()
                    unit = unit.isEmpty() ? null : unit
                }
            } else
                quantity = measurementType
        }
        return [quantity: quantity, unit: unit]
    }

    /**
     * Convert a phrase into camel case, suitable for using as a term.
     *
     * @param source The source string
     * @param firstUpper The first alpha character is upper case?
     *
     * @return A camel-cased string with non alphanumeric characters removed
     */
    String camelCase(String source, boolean firstUpper) {
        Reader reader = new StringReader(source)
        Writer writer = new StringWriter(source.length())
        boolean upper = firstUpper
        boolean word = false
        int ch

        while ((ch = reader.read()) >= 0) {
            if (Character.isLetterOrDigit(ch)) {
                if (upper) {
                    ch = Character.toUpperCase(ch)
                    upper = false
                } else
                    ch = Character.toLowerCase(ch)
                word = true
                writer.write(ch)
            } else if (ch == '\'' && word) {
                // Skip embedded apostrophe
            } else {
                upper = upper || word
                word = false
            }
        }
        return writer.toString()
    }

    /**
     * See if we can work out what sort of unit this
     * <p>
     *     The unit is first parsed using the {@link UnitFormat.DefaultFormat} and
     *     {@link UnitFormat.ASCIIFormat} formats and, if it can find a proper unit to
     *     match it against.
     *     If that fails, then a new base unit with the unit name is created; this
     *     allows unknown units to be passed through, as required.
     *
     * @param unit The unit string
     *
     * @return The matching unit
     */
    Unit<?> parseUnit(String unit) {
        Unit<?> u = null
        Unit<?> lookup

        if (unit == null)
            return null
        unit = unit.replace('\u03bc', '\u00b5') // Replace greek µ with lating micro sign µ
        lookup = unitDictionary[unit]
        if (lookup != null)
            return lookup
        try {
            u = BASE_FORMAT.parseObject(unit)
        } catch (ParseException ex) {
        }
        if (u == null) {
            try {
                u = ASCII_FORMAT.parseObject(unit)
            } catch (ParseException ex) {
            }
        }
        if (u == null)
            u = new BaseUnit<?>(unit)
        return u
    }


}

