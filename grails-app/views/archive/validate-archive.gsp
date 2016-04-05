<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:message code="page.validate-archive.title"/></title>
    <meta name="layout" content="main"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'dwc-archive.css')}" type="text/css">
</head>

<body>
<div id="main-content" class="container-fluid">
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div id="headingBar" class="heading-bar">
        <h1><g:message code="page.validate-archive.title"/></h1>
        <p><g:message code="page.validate-archive.blurb"/></p>
    </div>

    <div id="dwca-validator">
        <g:hasErrors bean="${configuration}">
            <div class="errors">
                <g:renderErrors bean="${configuration}" as="list" />
            </div>
        </g:hasErrors>
        <g:uploadForm>
            <fieldset>
                <div class="text-input">
                    <label for="check_source"><g:message code="page.label.source"/></label>
                    <g:field type="url" id="check_source" name="source" class="form-control source-url"
                             value="${configuration?.source}" title="${message(code: 'page.label.source.detail')}"/>
                    <label for="archive_file"><g:message code="page.label.sourceFile"/></label>
                    <g:field type="file" id="archive_file" name="sourceFile" class="form-control"
                             value="${configuration?.sourceFile?.originalFilename}" title="${message(code: 'page.label.sourceFile.detail')}"/>
                </div>
                <div class="checkbox-input">
                    <g:checkBox id="fix_bdrs" name="fixBdrs" class="form-control"
                                value="${configuration?.fixBdrs}"
                                title="${message(code: 'page.label.fixBdrs.detail')}"/>
                    <label for="fix_bdrs"><g:message code="page.label.fixBdrs"/></label>
                </div>
            </fieldset>
            <fieldset>
                <div class="checkbox-input">
                    <g:checkBox id="check_records" name="checkRecords" class="form-control"
                                value="${configuration?.checkRecords}"
                                title="${message(code: 'page.label.checkRecords.detail')}"/>
                    <label for="check_records"><g:message code="page.label.checkRecords"/></label>
                </div>

                <div class="checkbox-input">
                    <g:checkBox id="check_unique_terms" name="checkUniqueTerms" class="form-control"
                                value="${configuration?.checkUniqueTerms}"
                                title="${message(code: 'page.label.checkUniqueTerms.detail')}"/>
                    <label for="check_unique_terms"><g:message code="page.label.checkUniqueTerms"/></label>
                </div>

                <div class="text-input">
                    <label for="unique_terms"><g:message code="page.label.uniqueTerms"/></label>
                    <g:textField id="unique_terms" name="uniqueTermList" class="form-control"
                                value="${configuration?.uniqueTermList}"
                                title="${message(code: 'page.label.uniqueTerms.detail')}"/>
                </div>

                <div class="checkbox-input">
                    <g:checkBox id="check_images" name="checkImages" class="form-control"
                                value="${configuration?.checkImages}"
                                title="${message(code: 'page.label.checkImages.detail')}"/>
                    <label for="check_images"><g:message code="page.label.checkImages"/></label>
                </div>

                <div class="checkbox-input">
                    <g:checkBox id="check_presence" name="checkPresence" class="form-control"
                                value="${configuration?.checkPresence}"
                                title="${message(code: 'page.label.checkPresence.detail')}"/>
                    <label for="check_presence"><g:message code="page.label.checkPresence"/></label>
                </div>
            </fieldset>

            <div class="form-submit">
                <g:actionSubmit action="checkArchive" value="${message(code: 'page.label.validate')}"/>
            </div>
        </g:uploadForm>
    </div>

</div>
</body>
</html>