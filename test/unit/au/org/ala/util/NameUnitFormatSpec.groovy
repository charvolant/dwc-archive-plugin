package au.org.ala.util

import au.org.ala.util.units.NameUnitFormat
import spock.lang.Specification

import javax.measure.unit.NonSI
import javax.measure.unit.SI


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class NameUnitFormatSpec extends Specification {
    def format

    def setup() {
        format = NameUnitFormat.DEFAULT_NAME_FORMAT
    }

    def cleanup() {
    }

    void "test format unit 1"() {
        expect:
        format.format(SI.METRE) == "metre"
        format.format(SI.AMPERE) == "ampere"
        format.format(SI.COULOMB) == "coulomb"
        format.format(SI.KELVIN) == "kelvin"
        format.format(SI.SECOND) == "second"
        format.format(SI.VOLT) == "volt"
        format.format(SI.CELSIUS) == "celsius"
    }

    void "test format unit 2"() {
        expect:
        format.format(NonSI.BAR) == "bar"
        format.format(NonSI.HOUR) == "hour"
        format.format(NonSI.DEGREE_ANGLE) == "degree_of_angle"
        format.format(NonSI.YEAR) == "year"
        format.format(NonSI.MINUTE) == "minute"
        format.format(NonSI.MINUTE_ANGLE) == "minute_of_angle"
    }


    void "test format unit 3"() {
        expect:
        format.format(SI.CENTIMETER) == "centimetre"
        format.format(SI.MILLIMETER) == "millimetre"
        format.format(SI.HECTO(SI.PASCAL)) == "millibar" // Fine, if you say so
        format.format(SI.GIGA(NonSI.BYTE)) == "gigabyte"
        format.format(SI.MICRO(SI.SECOND)) == "microsecond"
        format.format(SI.KILO(SI.GRAM)) == "kilogram"
    }

    void "test format unit 4"() {
        expect:
        format.format(SI.WATT.times(NonSI.HOUR)) == "watt hour"
        format.format(SI.NEWTON.times(SI.METER)) == "newton metre"
        format.format(SI.PASCAL.times(SI.METER)) == "pascal metre"
    }

    void "test format unit 5"() {
        expect:
        format.format(SI.METER.times(SI.METER)) == "metre squared"
        format.format(SI.METER.pow(2)) == "metre squared"
        format.format(SI.SECOND.pow(3)) == "second cubed" // m^3 becomes kilolitre
        format.format(SI.METER.pow(4)) == "metre to the 4"
    }

    void "test format unit 6"() {
        expect:
        format.format(SI.METER.root(2)) == "metre to the root of 2"
    }

    void "test format unit 7"() {
        expect:
        format.format(SI.METER.divide(SI.SECOND)) == "metre per second"
        format.format(SI.METER.divide(SI.SECOND.times(SI.SECOND))) == "metre per second squared"
        format.format(SI.KELVIN.pow(-1)) == "per kelvin"
        format.format(SI.METER.pow(-2)) == "per metre squared"
    }

}