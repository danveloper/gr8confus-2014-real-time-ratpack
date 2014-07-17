import gr8conf.*
import ratpack.form.Form
import ratpack.remote.RemoteControlModule


import static groovy.json.JsonOutput.toJson
import static ratpack.groovy.Groovy.ratpack
import static ratpack.websocket.WebSockets.websocket

ratpack {

  bindings {
    //bind PhotoService, new DiskBackedPhotoService()
    bind PhotoService, new InMemoryPhotoService()
    bind EventBroadcaster, new EventBroadcaster()
    add new RemoteControlModule()
  }

  handlers {

    prefix("api") {
      post { PhotoService photoService, EventBroadcaster broadcaster ->
        def form = parse(Form)
        def wsContext = form.context as String
        def photo = form.file("photo")
        def id = photoService.save(photo.bytes)

        // broadcast the new photo
        broadcaster.broadcast "ws/$wsContext", id

        byContent {
          json {
            response.send toJson([name: id])
          }
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
