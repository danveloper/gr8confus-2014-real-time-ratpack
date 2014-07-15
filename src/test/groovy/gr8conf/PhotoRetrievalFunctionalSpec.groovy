package gr8conf

import ratpack.test.remote.RemoteControl

class PhotoRetrievalFunctionalSpec extends FunctionalSpec {
  RemoteControl remoteControl = new RemoteControl(aut)

  void "should get photo bytes back from call to get/:id"() {
    setup:
      def id = remoteControl.exec {
        get(PhotoService).save([1, 2, 3] as byte[])
      }

    when:
      def resp = get("api/$id").andReturn()

    then:
      resp.statusCode == 200
      resp.body.asByteArray() == [1, 2, 3]
  }
}
