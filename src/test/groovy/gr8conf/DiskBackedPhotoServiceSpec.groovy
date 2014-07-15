package gr8conf

import spock.lang.Specification
import spock.lang.Subject

class DiskBackedPhotoServiceSpec extends Specification {

  @Subject service = new DiskBackedPhotoService()

  void "should write photo bytes to temp dir on disk"() {
    setup:
      def bytes = [1, 2, 3] as byte[]

    when:
      def id = service.save(bytes)

    then:
      service.tmpDir.resolve("ratpack-${id}.jpg").toFile().exists()
  }

  void "should retrieve photo bytes from disk"() {
    setup:
      def bytes = [1, 2, 3] as byte[]

    when:
      def id = service.save(bytes)

    then:
      service.get(id) == bytes
  }

  void "delete should remove photo bytes from disk"() {
    setup:
      def bytes = [1, 2, 3] as byte[]
      def id = service.save(bytes)

    when:
      service.delete(id)

    then:
      service.get(id) == null
  }
}
