package gr8conf.ratpack.messaging

import com.google.inject.Inject
import com.google.inject.Injector
import gr8conf.ratpack.messaging.internal.*
import java.util.concurrent.CopyOnWriteArraySet
import javax.jms.ConnectionFactory
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.camel.*
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.jms.JmsComponent
import ratpack.guice.Guice
import ratpack.registry.Registry

class RatpackMessaging {
  private final Registry registry
  private final CamelContext context

  @Inject
  RatpackMessaging(Injector injector, CamelContext context) {
    this.registry = Guice.justInTimeRegistry(injector)
    this.context = context
  }

  void init() {
    Map<String, List<MessageHandler>> map = [:]

    for (MessageHandler handler in registry.getAll(MessageHandler)) {
      if (!map.containsKey(handler.url)) {
        map[handler.url] = new CopyOnWriteArraySet()
      }
      map[handler.url] << handler
    }

    ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")
    context.addComponent("jms", JmsComponent.jmsComponent(connectionFactory))
    context.start()
    context.addRoutes new RouteBuilder() {
      @Override
      void configure() throws Exception {
        for (entry in map) {
          def url = entry.key
          def handlers = entry.value
          from(url).process(new MessageHandlerAdapter(registry, handlers as MessageHandler[]))
        }
      }
    }
    context
  }

  static class MessageHandlerAdapter implements Processor {
    final Registry registry
    final MessageHandler[] messageHandlers

    MessageHandlerAdapter(Registry registry, MessageHandler[] messageHandlers) {
      this.messageHandlers = messageHandlers
      this.registry = registry
    }

    @Override
    void process(Exchange exchange) throws Exception {
      Map<String, String> headers = (Map<String, String>) exchange.getIn().getHeaders().collectEntries {
        [(it.key): it.value.toString()]
      }
      def request = new DefaultMessageRequest(headers, exchange.getIn().getBody(String))
      def response = new DefaultMessageResponse(exchange)
      def handler = messageHandlers[0]
      def context = new DefaultMessageContext(registry, request, response, messageHandlers, 1, handler)

      handler.handle(context)
    }
  }

}
