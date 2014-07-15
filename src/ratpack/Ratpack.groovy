import gr8conf.InMemoryPhotoService
import gr8conf.PhotoService
import java.util.concurrent.TimeUnit
import ratpack.form.Form
import ratpack.remote.RemoteControlModule


import static groovy.json.JsonOutput.toJson
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor
import static ratpack.groovy.Groovy.ratpack
import static ratpack.websocket.WebSockets.websocket

ratpack {

  bindings {
    //bind PhotoService, new DiskBackedPhotoService()
    bind PhotoService, new InMemoryPhotoService()
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

    get("ws") {
      websocket(context) { ws ->
        newSingleThreadScheduledExecutor().scheduleAtFixedRate({
          ws.send(new Date().toString())
        }, 0, 2, TimeUnit.SECONDS)
      } connect {}
    }

    assets "public", "index.html"
  }
}
