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
      post { PhotoService photoService ->
        def form = parse(Form)
        def photo = form.file("photo")
        def id = photoService.save(photo.bytes)
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

    get("broadcast") { EventBroadcaster broadcaster ->
      def message = request.queryParams.msg
      broadcaster.broadcast message
      response.send()
    }

    get("ws") { EventBroadcaster broadcaster ->
      websocket(context) { ws ->
        broadcaster.register {
          ws.send(it)
        }
      } connect {
        it.onClose {
          it.openResult.close()
        }
      }
    }

    assets "public", "index.html"
  }
}
