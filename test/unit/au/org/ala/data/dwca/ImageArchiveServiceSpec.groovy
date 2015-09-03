package au.org.ala.data.dwca

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ImageArchiveService)
@TestMixin(ServiceUnitTestMixin)
class ImageArchiveServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test check 1"() {
        when:
        ImageArchiveService service = new ImageArchiveService()
        service.transactionManager = Mock(PlatformTransactionManager) { getTransaction(_) >> Mock(TransactionStatus)}
        File location = new File("/home/pal155/tmp/August2015_2a_100")
        service.check(location)
        then:
        true
    }
}
