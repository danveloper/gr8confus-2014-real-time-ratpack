package gr8conf

interface PhotoService {

  String save(byte[] photo)

  byte[] get(String id)

  void delete(String id)
}