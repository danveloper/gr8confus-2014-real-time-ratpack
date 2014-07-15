package gr8conf

import com.jayway.restassured.response.Header
import com.jayway.restassured.specification.RequestSpecification
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import ratpack.groovy.test.TestHttpClient
import ratpack.groovy.test.TestHttpClients
import ratpack.test.remote.RemoteControl
import spock.lang.Specification

class FunctionalSpec extends Specification {

  LocalScriptApplicationUnderTest aut = new LocalScriptApplicationUnderTest('other.remoteControl.enabled': 'true')
  @Delegate TestHttpClient testHttpClient = TestHttpClients.testHttpClient(aut, { RequestSpecification spec ->
    spec.header(new Header("Accept", "text/html"))
  })
  RemoteControl remoteControl = new RemoteControl(aut)

  void "should get photo bytes back from call to get/:id"() {
    setup:
      def id = remoteControl.exec {
        get(PhotoService).save([1, 2, 3] as byte[])
      }

    when:
      def resp = get("api/$id").andReturn()

    then:
      resp.statusCode == 200
      resp.body.asByteArray() == [1, 2, 3]
  }
}
