package au.org.ala.util.units

import javax.measure.converter.UnitConverter
import javax.measure.unit.NonSI
import javax.measure.unit.ProductUnit
import javax.measure.unit.SI
import javax.measure.unit.Unit
import javax.measure.unit.UnitFormat

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class NameUnitFormat extends UnitFormat.DefaultFormat {
    static NameUnitFormat DEFAULT_NAME_FORMAT

    static IDENTIFIER_PATTERN = ~/[\\p{L}_]+/

    static final Map<String, Unit<?>> PREFIX_UNITS = [
            "ampere"   : SI.AMPERE,
            "becquerel": SI.BECQUEREL,
            "candela"  : SI.CANDELA,
            "celsius"  : SI.CELSIUS,
            "coulomb"  : SI.COULOMB,
            "farad"    : SI.FARAD,
            "gram"     : SI.GRAM,
            "gray"     : SI.GRAY,
            "henry"    : SI.HENRY,
            "hertz"    : SI.HERTZ,
            "joule"    : SI.JOULE,
            "katal"    : SI.KATAL,
            "kelvin"   : SI.KELVIN,
            "lumen"    : SI.LUMEN,
            "lux"      : SI.LUX,
            "metre"    : SI.METRE,
            "mole"     : SI.MOLE,
            "newton"   : SI.NEWTON,
            "ohm"      : SI.OHM,
            "pascal"   : SI.PASCAL,
            "radian"   : SI.RADIAN,
            "second"   : SI.SECOND,
            "siemens"  : SI.SIEMENS,
            "sievert"  : SI.SIEVERT,
            "steradian": SI.STERADIAN,
            "tesla"    : SI.TESLA,
            "volt"     : SI.VOLT,
            "watt"     : SI.WATT,
            "weber"    : SI.WEBER,
            "bar"      : NonSI.BAR,
            "litre"    : NonSI.LITRE,
            "byte"     : NonSI.BYTE
    ]
    static final Map<String, Unit<?>> OTHER_UNITS = [
            "atmosphere"     : NonSI.ATMOSPHERE,
            "day"            : NonSI.DAY,
            "decibel"        : NonSI.DECIBEL,
            "degree_of_angle": NonSI.DEGREE_ANGLE,
            "hour"           : NonSI.HOUR,
            "inch"           : NonSI.INCH,
            "tonne"          : NonSI.METRIC_TON,
            "minute"         : NonSI.MINUTE,
            "minute_of_angle": NonSI.MINUTE_ANGLE,
            "percent"        : NonSI.PERCENT,
            "second_of_angle": NonSI.SECOND_ANGLE,
            "week"           : NonSI.WEEK,
            "year"           : NonSI.YEAR
    ]
    static final Map<String, UnitConverter> PREFIXES = [
            "yotta": SI.E24,
            "zetta": SI.E21,
            "exa"  : SI.E18,
            "peta" : SI.E15,
            "tera" : SI.E12,
            "giga" : SI.E9,
            "mega" : SI.E6,
            "kilo" : SI.E3,
            "hecto": SI.E2,
            "deca" : SI.E1,
            "deci" : SI.Em1,
            "centi": SI.Em2,
            "milli": SI.Em3,
            "micro": SI.Em6,
            "nano" : SI.Em9,
            "pico" : SI.Em12,
            "femto": SI.Em15,
            "atto" : SI.Em18,
            "zepto": SI.Em21,
            "yocyo": SI.Em24
    ]

    static {
        DEFAULT_NAME_FORMAT = new NameUnitFormat()
        PREFIX_UNITS.each { u ->
            PREFIXES.each { p ->
                DEFAULT_NAME_FORMAT.label(u.value.transform(p.value), p.key + u.key)
            }
        }
        PREFIX_UNITS.each { u ->
            DEFAULT_NAME_FORMAT.label(u.value, u.key)
        }
        OTHER_UNITS.each { u ->
            DEFAULT_NAME_FORMAT.label(u.value, u.key)
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Format a unit, spelling out the units.
     */
    @Override
    Appendable format(Unit<?> unit, Appendable appendable) throws IOException {
        String name = nameFor(unit);
        if (name != null)
            return appendable.append(name);
        if (!(unit instanceof ProductUnit))
            throw new IllegalArgumentException("Cannot format given Object as a Unit");

        // Product unit.
        ProductUnit<?> productUnit = (ProductUnit<?>) unit;
        int invNbr = 0;

        // Write positive exponents first.
        boolean start = true;
        for (int i = 0; i < productUnit.getUnitCount(); i++) {
            int pow = productUnit.getUnitPow(i);
            if (pow >= 0) {
                if (!start) {
                    appendable.append(' '); // Separator.
                }
                name = nameFor(productUnit.getUnit(i));
                int root = productUnit.getUnitRoot(i);
                append(appendable, name, pow, root);
                start = false;
            } else {
                invNbr++;
            }
        }

        // Write negative exponents.
        if (invNbr != 0) {
            if (!start) {
                appendable.append(' ');
            }
            appendable.append("per ");
            start = true;
            for (int i = 0; i < productUnit.getUnitCount(); i++) {
                int pow = productUnit.getUnitPow(i);
                if (pow < 0) {
                    name = nameFor(productUnit.getUnit(i));
                    int root = productUnit.getUnitRoot(i);
                    if (!start) {
                        appendable.append(' '); // Separator.
                    }
                    append(appendable, name, -pow, root);
                    start = false;
                }
            }
        }
        return appendable;
    }


    private void append(Appendable appendable, CharSequence symbol,
                        int pow, int root) throws IOException {
        appendable.append(symbol);
        if ((pow != 1) || (root != 1)) {
            // Write exponent.
            if ((pow == 2) && (root == 1)) {
                appendable.append(" squared"); // Square
            } else if ((pow == 3) && (root == 1)) {
                appendable.append(" cubed"); // Cubic
            } else {
                // Use general exponent form.
                appendable.append(" to the ");
                if (pow != 1)
                    appendable.append(String.valueOf(pow));
                if (root != 1) {
                    appendable.append("root of ");
                    appendable.append(String.valueOf(root));
                }
            }
        }
    }


}