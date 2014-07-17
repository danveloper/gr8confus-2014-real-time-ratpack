package gr8conf

import com.google.inject.Inject
import gr8conf.ratpack.messaging.MessageContext
import gr8conf.ratpack.messaging.MessageHandler
import groovy.json.JsonSlurper

class PhotoMessageHandler implements MessageHandler {
  String url = "jms:queue:test.queue"

  private final EventBroadcaster broadcaster
  private final PhotoService photoService
  private final JsonSlurper slurper = new JsonSlurper()

  @Inject
  PhotoMessageHandler(EventBroadcaster broadcaster, PhotoService photoService) {
    this.broadcaster = broadcaster
    this.photoService = photoService
  }

  @Override
  void handle(MessageContext context) {
    Map<String, String> message = (Map<String, String>) slurper.parseText(context.request.message)
    def bytes = message.photo.decodeBase64()
    def id = photoService.save(bytes)
    broadcaster.broadcast(message.wsContext, id)
    context.next()
  }
}
