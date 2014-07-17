package gr8conf.ratpack.messaging

interface MessageResponse {
  void send(String message)

  void send(Map<String, String> headers, String message)
}
