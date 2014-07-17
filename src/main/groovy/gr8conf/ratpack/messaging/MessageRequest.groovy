package gr8conf.ratpack.messaging

interface MessageRequest {
  Map<String, String> getHeaders()

  String getMessage()
}