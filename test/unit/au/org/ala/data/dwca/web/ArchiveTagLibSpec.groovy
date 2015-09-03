package au.org.ala.data.dwca.web

import au.org.ala.data.dwca.Violation
import au.org.ala.data.dwca.web.ArchiveTagLib
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestFor(ArchiveTagLib)
class ArchiveTagLibSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test violation 1"() {
        when:
        def breach = new Violation(source: null, code: "archive.invalid")
        then:
        applyTemplate('<dwca:breach breach="${breach}"/>', [breach: breach]) == '<tr><td class="dwca-location"></td><td class="dwca-message">archive.invalid</td><td class="dwca-reference"></td>'

    }
}