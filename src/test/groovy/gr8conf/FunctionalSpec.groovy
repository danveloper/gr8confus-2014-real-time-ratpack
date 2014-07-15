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
}
