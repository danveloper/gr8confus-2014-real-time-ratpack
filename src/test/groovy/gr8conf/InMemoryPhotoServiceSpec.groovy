package gr8conf

import spock.lang.Specification
import spock.lang.Subject

class InMemoryPhotoServiceSpec extends Specification {

  @Subject
      service = new InMemoryPhotoService()

  void "should auto-generate unique photo id"() {
    setup:
      def bytes = [1, 2, 3] as byte[]

    when:
      def id = service.save(bytes)

    then:
      service.get(id).is(bytes)
  }

  void "delete should remove the byte array from storage"() {
    setup:
      def bytes = [1, 2, 3] as byte[]

    when:
      def id = service.save(bytes)

    then:
      service.get(id).is(bytes)

    when:
      service.delete(id)

    then:
      service.get(id).is(null)
  }
}
