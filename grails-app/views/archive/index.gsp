<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:message code="page.index.title"/></title>
    <meta name="layout" content="main"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'dwc-archive.css')}" type="text/css">
</head>

<body>
<div id="main-content" class="container-fluid">
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div id="headingBar" class="heading-bar">
        <h1><g:message code="page.index.title"/></h1>
        <p><g:message code="page.index.blurb"/></p>
    </div>

    <div id="archive-options">
        <h2><g:message code="page.index.services"/></h2>
        <ul>
            <li><g:link action="validateArchive"><g:message code="page.validate-archive.title"/></g:link></li>
            <li><g:link action="flattenMeasurementArchive"><g:message code="page.flatten-measurement-archive.title"/></g:link></li>
        </ul>
    </div>

    <div id="resources">
        <h2><g:message code="page.index.resources.header"/></h2>
        <p><g:message code="page.index.resources"/></p>
    </div>
</div>
</body>
</html>