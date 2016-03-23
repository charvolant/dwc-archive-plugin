<%@ page import="au.org.ala.data.value.ValueParser" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:message code="page.measurement-terms.title"/></title>
    <meta name="layout" content="main"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'dwc-archive.css')}" type="text/css">
</head>

<body>
<div id="main-content" class="container-fluid">
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div id="headingBar" class="recordHeader heading-bar">
        <h1><g:message code="page.measurement-terms.title"/></h1>

        <h2><g:message code="page.measurement.source" args="${[configuration.rootFileName]}"/></h2>
    </div>
    <g:if test="${configuration.terms.isEmpty()}">
        <div class="measurement-terms.missing">
            <h3><g:message code="page.measurement-terms.noTerms.heading"/></h3>

            <p><g:message code="page.measurement-terms.noTerms"/></p>
        </div>
    </g:if>
    <g:if test="${!configuration.terms.isEmpty()}">
        <div class="term-map">
            <g:form>
                <fieldset>
                    <g:hiddenField name="source" value="${configuration.source}"/>
                    <g:hiddenField name="rootFileName" value="${configuration.rootFileName}"/>
                    <p><g:message code="page.label.format"/>
                    <g:radioGroup name="format" values="${configuration.VALID_FORMATS}" labels="${configuration.VALID_FORMATS}" value="${configuration.format}">
                        <span title="${message(code: 'page.label.format.' + it.label + '.detail')}">${it.radio} <g:message code="${'page.label.format.' + it.label}"/></span>
                    </g:radioGroup>
                    </p>
                    <p><g:message code="page.label.filter"/>
                        <g:field type="textField" class="dwca-filter" name="filter" value="${configuration.filter?.asExpression()}" title="${message(code: 'page.label.filter.detail')}"/>
                    </p>
                    <p><g:message code="page.label.values"/>
                        <g:field type="textField" class="dwca-values" name="values" value="${au.org.ala.data.value.ValueParser.asString(configuration.values)}" title="${message(code: 'page.label.values.detail')}"/>
                    </p>
                    <p><g:message code="page.label.valueSeparator"/>
                    <g:field type="textField" name="valueSeparator" value="${configuration.valueSeparator}" title="${message(code: 'page.label.valueSeparator.detail')}"/>
                    </p>
                    <table class="table table-bordered table-striped table-condensed">
                        <tr>
                            <th><g:message code="page.label.measurementType"/></th>
                            <th><g:message code="page.label.measurementUnit"/></th>
                            <th><g:message code="page.label.matchedUnit"/></th>
                            <th><g:message code="page.label.term"/></th>
                        </tr>
                        <g:each in="${configuration.terms}" var="term" status="ts">
                            <tr>
                                <td><g:fieldValue field="measurementType" bean="${term}"/><g:hiddenField
                                        name="terms[${ts}].measurementType" value="${term.measurementType}"/></td>
                                <td><g:fieldValue field="measurementUnit" bean="${term}"/><g:hiddenField
                                        name="terms[${ts}].measurementUnit" value="${term.measurementUnit}"/></td>
                                <td><g:fieldValue field="unit" bean="${term}"/></td>
                                <td><g:field type="textField" name="terms[${ts}].term" value="${term.simpleName()}"
                                             class="dwca-term" title="${message(code: 'page.label.term.detail')}"/></td>
                            </tr>
                        </g:each>
                    </table>
                </fieldset>

                <div class="form-submit">
                    <g:actionSubmit action="flattenMeasurementArchiveTerms"
                                    value="${message(code: 'page.label.flatten')}"/>
                    <g:actionSubmit action="saveMeasurementArchiveTerms"
                                    value="${message(code: 'page.label.save')}"/>
                </div>
            </g:form>
        </div>
    </g:if>
</div>
</body>
</html>