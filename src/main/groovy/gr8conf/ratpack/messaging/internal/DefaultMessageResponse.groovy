package gr8conf.ratpack.messaging.internal

import gr8conf.ratpack.messaging.MessageResponse
import org.apache.camel.Exchange

class DefaultMessageResponse implements MessageResponse {
  private final Exchange exchange

  DefaultMessageResponse(Exchange exchange) {
    this.exchange = exchange
  }

  @Override
  void send(String message) {
    exchange.getOut().setBody(message)
  }

  @Override
  void send(Map<String, String> headers, String message) {
    exchange.getOut().setBody(message)
    exchange.getOut().setHeaders(headers)
  }
}
