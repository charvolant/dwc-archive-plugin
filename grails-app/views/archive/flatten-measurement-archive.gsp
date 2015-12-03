<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:message code="page.flatten-measurement-archive.title"/></title>
    <meta name="layout" content="main"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'dwc-archive.css')}" type="text/css">
</head>

<body>
<div id="main-content" class="container-fluid">
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div id="headingBar" class="heading-bar">
        <h1><g:message code="page.flatten-measurement-archive.title"/></h1>
        <p><g:message code="page.flatten-measurement-archive.blurb"/></p>
    </div>

    <div id="flatten-measturement-archive">
        <g:hasErrors bean="${configuration}">
            <div class="errors">
                <g:renderErrors bean="${configuration}" as="list" />
            </div>
        </g:hasErrors>
        <g:uploadForm>
            <fieldset>
                <div class="text-input">
                    <label for="archive_source"><g:message code="page.label.source"/></label>
                    <g:field type="url" id="archive_source" name="source" class="form-control source-url" placeholder="http://host.com/path/archive.zip"
                             value="${configuration?.source}" title="${message(code: 'page.label.source.detail')}"/>
                    <label for="archive_file"><g:message code="page.label.sourceFile"/></label>
                    <g:field type="file" id="archive_file" name="sourceFile" class="form-control"
                             value="${configuration?.sourceFile?.originalFilename}" title="${message(code: 'page.label.sourceFile.detail')}"/>
                </div>
            </fieldset>
            <fieldset>
                <div class="text-input">
                    <label for="archive_mapping"><g:message code="page.label.mappingFile"/></label>
                    <g:field type="file" id="archive_mapping" name="mappingFile" class="form-control"
                             value="${configuration?.mappingFile?.originalFilename}" title="${message(code: 'page.label.mappingFile.detail')}"/>
                </div>
            </fieldset>

            <div class="form-submit">
                <g:actionSubmit action="collectMeasurementTerms" value="${message(code: 'page.label.collect')}"/>
            </div>
        </g:uploadForm>
    </div>

    <div id="resources">
        <h2><g:message code="page.index.resources.header"/></h2>
        <p><g:message code="page.index.resources"/></p>
    </div>
</div>
</body>
</html>