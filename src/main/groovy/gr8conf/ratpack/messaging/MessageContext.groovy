package gr8conf.ratpack.messaging

import ratpack.registry.Registry

interface MessageContext {
  MessageContext getContext()

  abstract <O> O get(Class<O> type)

  MessageRequest getRequest()

  MessageResponse getResponse()

  void next()

  void insert(MessageHandler... handlers)

  void insert(Registry registry, MessageHandler... handlers)
}
