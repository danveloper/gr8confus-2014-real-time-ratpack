package gr8conf

import groovy.transform.CompileStatic
import java.util.concurrent.ConcurrentHashMap

@CompileStatic
class InMemoryPhotoService implements PhotoService {
  private final Map<String, byte[]> storage = new ConcurrentHashMap()

  @Override
  String save(byte[] photo) {
    def id = UUID.randomUUID().toString()
    storage[id] = photo
    id
  }

  @Override
  byte[] get(String id) {
    storage.containsKey(id) ? storage[id] : null
  }

  @Override
  void delete(String id) {
    if (storage.containsKey(id)) {
      storage.remove(id)
    }
  }
}
