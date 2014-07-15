import static ratpack.groovy.Groovy.ratpack

ratpack {

  handlers {

    handler {
      if (request.headers.'User-Agent' ==~ /.*Chrome.*/) {
        response.status 418
        response.send 'text/html', "<h1>You're not welcome here, Chrome.</h1>"
      } else {
        next()
      }
    }

    handler {
      byMethod {

        get {
          response.send "text/html", """\
            |<!DOCTYPE html>
            |<html><body>
            |<h1>Hello World!</h1>
            |</body></html>
          """.stripIndent().stripMargin()
        }

        // curl -X POST localhost:5050
        post {
          response.send "text/plain", """\
            |Got your message :-)
          """.stripIndent().stripMargin()
        }

        // curl -X PUT localhost:5050
        put {
          response.send "text/plain", """\
            |Received your update :-)
          """.stripIndent().stripMargin()
        }

        // curl -X PATCH localhost:5050
        patch {
          response.send "text/plain", """\
            |Got your patch :-)
          """.stripIndent().stripMargin()
        }

        // curl -X DELETE localhost:5050
        delete {
          response.send "text/plain", """\
            |Handling that delete now... :-)
          """.stripIndent().stripMargin()
        }
      }
    }
  }
}
