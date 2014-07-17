package gr8conf

import groovy.transform.CompileStatic
import java.nio.file.Files
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake

class WebSocketSpec extends FunctionalSpec {

  void "should get photo refs back from websocket endpoint"() {
    setup:
      def uri = new URI(aut.address.toString() + "ws/default")
      def wsClient = new RecordingWebSocketClient(uri)
      wsClient.connectBlocking()
      def photo = Files.createTempFile("foo", "bar")
      photo.write("foo")

    when:
      request.multiPart("photo", photo.toFile()).formParam("context", "default")
      post("api")
      def message = wsClient.received.poll(2, TimeUnit.SECONDS)

    then:
      message

    when:
      wsClient.closeBlocking()

    then:
      if (wsClient.exception != null) {
        throw wsClient.exception
      }
  }

  @CompileStatic
  static class RecordingWebSocketClient extends WebSocketClient {

    Exception exception

    final LinkedBlockingQueue<String> received = new LinkedBlockingQueue<String>()

    RecordingWebSocketClient(URI serverURI) {
      super(serverURI)
    }

    @Override
    void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    void onMessage(String message) {
      received.put message
    }

    @Override
    void onClose(int code, String reason, boolean remote) {
      if (remote) {
        exception = new Exception("Server initiated close: $code $reason")
      }
    }

    @Override
    void onError(Exception ex) {
      exception = ex
    }
  }

}
