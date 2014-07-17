package gr8conf

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import gr8conf.ratpack.messaging.RatpackMessaging
import org.apache.activemq.broker.BrokerService
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext

class MessagingModule extends AbstractModule {
  @Override
  protected void configure() {
    BrokerService broker = new BrokerService()
    broker.addConnector("tcp://localhost:61616")
    broker.start()

    bind(CamelContext).toInstance(new DefaultCamelContext())
    bind(PhotoMessageHandler).in(Scopes.SINGLETON)
    bind(RatpackMessaging).in(Scopes.SINGLETON)
  }
}
