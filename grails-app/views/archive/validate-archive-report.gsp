<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:message code="page.validate-archive-report.title"/></title>
    <meta name="layout" content="main"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'dwc-archive.css')}" type="text/css">
</head>
<body>
<div id="main-content" class="container-fluid">
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div id="headingBar" class="recordHeader heading-bar">
        <h1><g:message code="page.validate-archive-report.title"/></h1>
        <h2><g:message code="page.validate-archive-report.source" args="${[configuration.sourceName]}"/></h2>
    </div>
    <div id="report-confguration">
        <h3><g:message code="page.label.checks"/></h3>
        <table class="table table-bordered table-striped table-condensed">
            <tr><td><g:message code="page.label.checkRecords"/></td><td><g:fieldValue bean="${configuration}" field="checkRecords"/></td></tr>
            <tr><td><g:message code="page.label.checkUniqueTerms"/></td><td><g:fieldValue bean="${configuration}" field="checkUniqueTerms"/></td></tr>
            <tr><td><g:message code="page.label.uniqueTerms"/></td><td><g:fieldValue bean="${configuration}" field="uniqueTermList"/></td></tr>
            <tr><td><g:message code="page.label.checkImages"/></td><td><g:fieldValue bean="${configuration}" field="checkImages"/></td></tr>
            <tr><td><g:message code="page.label.checkPresence"/></td><td><g:fieldValue bean="${configuration}" field="checkPresence"/></td></tr>
        </table>
    </div>
    <div class="dwca-info">
        <h3><g:message code="page.label.information"/></h3>
        <table class="table table-bordered table-striped table-condensed">
            <tr><td><g:message code="page.label.totalRecords"/></td><td><g:fieldValue bean="${report}" field="records"/></td></tr>
            <tr><td><g:message code="page.label.totalImages"/></td><td><g:fieldValue bean="${report}" field="images"/></td></tr>
         </table>
    </div>
    <g:if test="${!report.hasViolations() && !report.hasCautions()}">
        <div class="dwca-ok">
            <h3><g:message code="page.validate-archive-report.noProblems.heading"/></h3>
            <p><g:message code="page.validate-archive-report.noProblems"/></p>
        </div>
    </g:if>
    <g:if test="${report.hasViolations()}">
        <div class="dwca-violations">
            <h3><g:message code="page.label.violations"/></h3>
            <table class="table table-bordered table-striped table-condensed">
                <tr>
                    <th><g:message code="page.label.file"/></th>
                    <th><g:message code="page.label.error"/></th>
                    <th><g:message code="page.label.more"/></th>
                </tr>
                <g:each in="${report.violations}" var="item">
                    <dwca:breach breach="${item}"/>
                </g:each>
            </table>
        </div>
    </g:if>
    <g:if test="${report.hasCautions()}">
        <div class="dwca-cautions">
            <h3><g:message code="page.label.cautions"/></h3>
            <table class="table table-bordered table-striped table-condensed">
                <tr>
                    <th><g:message code="page.label.file"/></th>
                    <th><g:message code="page.label.warning"/></th>
                    <th><g:message code="page.label.more"/></th>
                </tr>
                <g:each in="${report.cautions}" var="item">
                    <dwca:breach breach="${item}"/>
                </g:each>
            </table>
        </div>
    </g:if>
</div>
</body>
</html>