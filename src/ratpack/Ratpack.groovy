import gr8conf.*
import gr8conf.ratpack.messaging.RatpackMessaging
import org.apache.camel.CamelContext
import ratpack.form.Form
import ratpack.remote.RemoteControlModule


import static groovy.json.JsonOutput.toJson
import static ratpack.groovy.Groovy.ratpack

ratpack {

  bindings {
    //bind PhotoService, new DiskBackedPhotoService()
    bind PhotoService, new InMemoryPhotoService()
    bind EventBroadcaster, new EventBroadcaster()
    add new RemoteControlModule()
    add new MessagingModule()

    init { RatpackMessaging messaging ->
      messaging.init()
    }
  }

  handlers {

    prefix("api") {
      post { CamelContext camelContext ->
        def form = parse(Form)
        def wsContext = form.context as String
        def photo = form.file("photo")

        blocking {
          def template = camelContext.createProducerTemplate()
          template.sendBody("jms:queue:test.queue",
              toJson(
                  [photo    : photo.bytes.encodeBase64().toString(),
                   wsContext: "ws/$wsContext"]))
        } then {
          response.status 202
          response.send()
        }

      }

      get(":id") { PhotoService photoService ->
        blocking {
          photoService.get(pathTokens.id)
        } then { photo ->
          response.send("image/png", photo)
        }
      }
    }

    get("ws/:wsContext") { EventBroadcaster broadcaster ->
      broadcaster.register context
    }

    assets "public", "index.html"
  }
}
