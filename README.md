#DwC Archive Plugin

A plugin for the ala-collectory that provides tools for
checking [Darwin Core Archives](http://www.gbif.org/resource/80636)
(DwCA) for various useful features that the ALA needs.

##Functions

Functions are accessible from the /archive path for a user interface
and the /ws/archive path for a web service.

###Start page

/archive

Provides a user interface page so that a request can be filled out.

###Archive Check

/archive/check or /ws/archive/check

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

##Dependencies

* https://github.com/AtlasOfLivingAustralia/ala-collectory

##Configuration

The checker uses a a work directory for unzipping archives.
This is specified by the `workDir` configuration property.
The plugin cleans up after itself, so data in the work directory is removed
after analysis.