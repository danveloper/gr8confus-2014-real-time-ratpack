import gr8conf.DiskBackedPhotoService
import gr8conf.InMemoryPhotoService
import gr8conf.PhotoService
import ratpack.form.Form


import static groovy.json.JsonOutput.toJson
import static ratpack.groovy.Groovy.ratpack

ratpack {

  bindings {
    //bind PhotoService, new DiskBackedPhotoService()
    bind PhotoService, new InMemoryPhotoService()
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

    assets "public", "index.html"
  }
}
