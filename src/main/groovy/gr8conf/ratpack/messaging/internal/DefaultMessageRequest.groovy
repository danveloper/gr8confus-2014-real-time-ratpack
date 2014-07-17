package gr8conf.ratpack.messaging.internal

import gr8conf.ratpack.messaging.MessageRequest
import groovy.transform.Immutable

@Immutable
class DefaultMessageRequest implements MessageRequest {
  final Map<String, String> headers
  final String message
}
