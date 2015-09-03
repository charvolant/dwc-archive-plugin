package au.org.ala.data.dwca.web

import au.org.ala.data.dwca.Breach
import org.springframework.web.util.HtmlUtils

class ArchiveTagLib {
    def messageSource

    static defaultEncodeAs = [taglib:'text']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    static namespace = "dwca"

    def breach = { attrs ->
        if (attrs.breach) {
            def breach = attrs.breach as Breach
            out << "<tr>"
            out << "<td class=\"dwca-location\">"
            if (breach.source)
                out << HtmlUtils.htmlEscape(breach.source.location)
            out << "</td>"
            out << "<td class=\"dwca-message\">"
            out << HtmlUtils.htmlEscape(messageSource.getMessage(breach.code, breach.params, breach.code, null))
            def detail = messageSource.getMessage(breach.code + ".detail", breach.params, "", null)
            if (!detail.isEmpty()) {
                def info = messageSource.getMessage("report.page.informationLink", null, "...", null)
                out << '&nbsp;<span class="dwca-detail" title="'
                out << HtmlUtils.htmlEscape(detail)
                out << '">'
                out << HtmlUtils.htmlEscape(info)
                out << '</span>'
            }
            out << "</td>"
            out << "<td class=\"dwca-reference\">"
            def href = messageSource.getMessage(breach.code + ".href", breach.params, "", null)
            if (!href.isEmpty()) {
                def ref = messageSource.getMessage("report.page.referenceLink", null, "...", null)
                def refDetail = messageSource.getMessage("report.page.referenceLink.detail", null, "Reference", null)
                out << "<span><a title=\"${HtmlUtils.htmlEscape(refDetail)}\" href=\"${href}\">${HtmlUtils.htmlEscape(ref)}</a></span>"
            }
            out << "</td>"
        }
    }
}
