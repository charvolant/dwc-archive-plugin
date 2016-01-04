#DwC Archive Plugin

A plugin for the ala-collectory that provides tools for
checking [Darwin Core Archives](http://www.gbif.org/resource/80636)
(DwCA) for various useful features that the ALA needs.
 

## Functions

Functions are accessible from the /archive path for a user interface
and the /ws/archive path for a web service.

### Start page

`/archive`

Provides the root of the user interface pages.

### Archive Validation

`/archive/validateArchive` (UI) or `/ws/archive/validateArchive` (WS)

Checks a DwCA for suitability using the following parameters:

|Name|Description|Default|
|----|-----------|-------|
|source|A URL pointing to the DwCA|(required)|
|checkRecords|Check individual records for validity|true|
|checkUniqueTerms|Check occurrence records to ensure that they have a unique key for loading into the ALA|true|
|uniqueTermList|A comma-separated list of DwC terms for key building|catalogNumber|
|checkImages|Check to see if there is a usable image extension|true|
|checkPresence|Check image presence in the archive if it is listed in the image extension|true|

These parameters can be either part of a GET parameter list or a POST form.

The user interface returns a HTML report. The web service returns a JSON report by default,
using a .json, .xml or .html extension will return a suitably formatted report.
Eg. `/ws/archive/check.xml?source=http://somewhere.com/archive.zip` will return an XML report.

### Flatten Archive

`/archive/flattenMeasurementArchive` (UI) or `/ws/archive/flatten` (WS)

Converts a DWCA with a MeasurementOrFact extension file into a single, flattened file, with the measurements
converted into extra columns.
The UI version provides an initial analysis and then allows editing of terms.
The web-service version provides a straight-through service, possibly with user-defined mappings provided in
a mapping file.

| Name | Description | Default |
| ---- | ----------- | ------- |
| source | A URL pointing to the DWCA | |
| sourceFile | An uploaded DWCA file (via multipart/form-data) | |
| mappingFile | A JSON configuration and mapping file (via multipart/form-data) -- see below for content | |
| filter | A filter to apply to the entries -- see below for content | |
| format | Either `csv` or `dwca` for results in either CSV or DWCA form | csv |
| allowNewTerms | If true, then the flattening will continue of unrecognised terms are encountered, with auto-generated mapping. If false, an error is returned if an unrecognised term is encountered | true | 

#### Mapping File

The mapping file is a JSON file that contains a description of how to map measurement types onto terms suitable for loading into the ALA.
An example mapping file is:

```
{
  "terms": [
    {
      "term":"availablePhosphate",
      "uri":"http://vocabulary.ala.org.au/availablePhosphate",
      "measurementType":"Available Phosphate (mg/L)",
    }
  ],
  "filter":"basisOfRecord == \"HumanObservation\""
}
```

The `terms` list contrains a mapping from a `measurementType` value to a term that becomes a column in the flattened file.
In this case a measurement with a measurementType of `Available Phosphate (mg/L)` will appear as a column labelled
`availablePhosphate`.
If a term is not in the terms list and `allowNewTerms` is true, a term will be generated from the measurement type.
The generated term will try and camel-case the measurement type and spell out the unit. For example,
`Dissolved Oxygen (mg/L)` would become `dissolvedOxygenInMilligramPerLitre`

If you are using the UI, the terms will be collected and displayed before the archive is flattened, so that you
can massage the term names and map accidentally different but the same measurement types onto a single term.
You can save the mapping for future use.

#### Filters

Filters are fairly restrictive expressions that allow you to select occurrence records based on their content.
The basic elements of the filters, in order of precedence, are:

| Element | Syntax | Description | Example |
| ------- | ------ | ----------- | ------- |
| term | term | A term (column name) preferentially from the core but also from an extension | basisOfRecord |
| string | "string" | A literal string | "HumanObservation" |
| equality test | == | Test to see if a term has a value | basisOfRecord == "HumanObservation" |
| negation | not | Logical negation | not basisOfRecord == "HumanObservation" |
| conjunction | and| Logical conjunction | basisOfRecord == "HumanObservation" and catalogNumber == "1234" |
| disjunction | or | Logical disjunction | basisOfRecord == "HumanObservation" or basisOfRecord == "Observation" |
| grouping | ( ) | Expression grouping | not (basisOfRecord == "HumanObservation" or basisOfRecord == "Observation") |

#### Curl usage

Flattening can be invoked via curl with the following curl command:

`curl -o flattened.zip -F sourceFile=@orginal.zip -F mappingFile=@mapping.json -F format=dwca http://host/ws/archive/flatten`

## Dependencies

To use the plugin, add `runtime ":dwc-archive:0.1-SNAPSHOT"` to the plugins list in `BuildConfig.groovy`

* https://github.com/AtlasOfLivingAustralia/ala-collectory

## Configuration

The checker uses a a work directory for unzipping archives.
This is specified by the `workDir` configuration property.
The plugin cleans up after itself, so data in the work directory is removed
after analysis.