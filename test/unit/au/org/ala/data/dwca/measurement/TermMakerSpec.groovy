package au.org.ala.data.dwca.measurement

import spock.lang.Specification

import javax.measure.unit.BaseUnit
import javax.measure.unit.NonSI
import javax.measure.unit.SI


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class TermMakerSpec extends Specification {
    TermMaker termMaker

    def setup() {
        termMaker = new TermMaker(["deg C": SI.CELSIUS, "% sat": NonSI.PERCENT])
    }

    def cleanup() {
    }

    void "test describe unit 1"() {
        expect:
        termMaker.parseUnit("m") == SI.METER
        termMaker.parseUnit("mm") == SI.MILLIMETER
        termMaker.parseUnit("μPa") == SI.MICRO(SI.PASCAL)
        termMaker.parseUnit("m/s") == SI.METERS_PER_SECOND
        termMaker.parseUnit("kg*m*s^-2") == SI.KILOGRAM.times(SI.METERS_PER_SQUARE_SECOND)
        termMaker.parseUnit("mg/L") == SI.MILLI(SI.GRAM).divide(NonSI.LITER)
     }

    void "test camel case 1"() {
        expect:
        termMaker.camelCase("Once Upon a time", false) == "onceUponATime"
        termMaker.camelCase("This WORD is in CAPITALS", false) == "thisWordIsInCapitals"
        termMaker.camelCase("Something, elsewhere. Somehow", false) == "somethingElsewhereSomehow"
        termMaker.camelCase("I think (but. I don't know)", false) == "iThinkButIDontKnow"
        termMaker.camelCase("Something, elsewhere. Somehow?", false) == "somethingElsewhereSomehow"
        termMaker.camelCase("  Something, elsewhere. Somehow?  ", false) == "somethingElsewhereSomehow"
    }

    void "test camel case 2"() {
        expect:
        termMaker.camelCase("Once Upon a time", true) == "OnceUponATime"
        termMaker.camelCase("This WORD is in CAPITALS", true) == "ThisWordIsInCapitals"
        termMaker.camelCase("Something, elsewhere. Somehow", true) == "SomethingElsewhereSomehow"
        termMaker.camelCase("I think (but. I don't know)", true) == "IThinkButIDontKnow"
        termMaker.camelCase("Something, elsewhere. Somehow?", true) == "SomethingElsewhereSomehow"
        termMaker.camelCase("  Something, elsewhere. Somehow?  ", true) == "SomethingElsewhereSomehow"
    }

    void "test convert type to term 1"() {
        expect:
        termMaker.convertTypeToTerm("Is this a measure?", null).simpleName() == "isThisAMeasure"
        termMaker.convertTypeToTerm("No. of volunteers testing", null).simpleName() == "noOfVolunteersTesting"
        termMaker.convertTypeToTerm("Water type", null).simpleName() == "waterType"
    }

    void "test convert type to term 2"() {
        expect:
        termMaker.convertTypeToTerm("Is this a measure?", null).qualifiedName() == "http://vocabulary.ala.org.au/isThisAMeasure"
        termMaker.convertTypeToTerm("No. of volunteers testing", null).qualifiedName() == "http://vocabulary.ala.org.au/noOfVolunteersTesting"
        termMaker.convertTypeToTerm("Water type", null).qualifiedName() == "http://vocabulary.ala.org.au/waterType"
    }

    void "test convert type to term 3"() {
        expect:
        termMaker.convertTypeToTerm("Dissolved Oxygen (mg/L)", null).simpleName() == "dissolvedOxygenInMilligramPerLitre"
        termMaker.convertTypeToTerm("Air Temperature (deg C)", null).simpleName() == "airTemperatureInCelsius"
        termMaker.convertTypeToTerm("Turbidity (NTUs)", null).simpleName() == "turbidityInNtus"
        termMaker.convertTypeToTerm("Electrical Conductivity (µS/cm)", null).simpleName() == "electricalConductivityInMicrosiemensPerCentimetre"
        termMaker.convertTypeToTerm("Survey duration (hrs)", null).simpleName() == "surveyDurationInHrs"
        termMaker.convertTypeToTerm("Dissolved Oxygen (% sat)", null).simpleName() == "dissolvedOxygenInPercent"
        termMaker.convertTypeToTerm("pH (pH units)", null).simpleName() == "phInPhUnits"
    }

    void "test convert type to term 4"() {
        expect:
        termMaker.convertTypeToTerm("Dissolved Oxygen (mg/L)", null).unit == SI.MILLI(SI.GRAM).divide(NonSI.LITER)
        termMaker.convertTypeToTerm("pH (pH units)", null).unit == new BaseUnit("pH units")
    }


}