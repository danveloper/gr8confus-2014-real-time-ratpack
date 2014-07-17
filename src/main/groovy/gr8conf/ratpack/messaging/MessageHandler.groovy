package gr8conf.ratpack.messaging

interface MessageHandler<T> {

  String getUrl()

  void handle(MessageContext context)
}
