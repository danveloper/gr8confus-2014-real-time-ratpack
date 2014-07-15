package gr8conf

import groovy.transform.CompileStatic
import java.nio.file.Files
import java.nio.file.Path
import ratpack.exec.ExecController


import static java.nio.file.Files.write

@CompileStatic
class DiskBackedPhotoService implements PhotoService {
  private static final String PREFIX = "ratpack-"
  private static final String SUFFIX = ".jpg"
  final Path tmpDir = File.createTempDir().toPath()

  @Override
  String save(byte[] photo) {
    Path dest = Files.createTempFile(tmpDir, PREFIX, SUFFIX)
    write dest, photo
    dest.fileName.toString().replaceAll("^${PREFIX}", "").replaceAll("${SUFFIX}\$", "")
  }

  @Override
  byte[] get(String id) {
    try {
      tmpDir.resolve(getFileName(id)).bytes
    } catch (IOException IGNORE) {
      null
    }
  }

  @Override
  void delete(String id) {
    def path = tmpDir.resolve getFileName(id)
    Files.delete(path)
  }

  private static String getFileName(String name) {
    "${PREFIX}${name}${SUFFIX}"
  }
}
