package filesystems;

import java.util.Arrays;

public final class DocDataFile extends DocFile {

  private byte[] array;

  public DocDataFile(String name, byte[] array) {
    super(name);
    this.array = array;

  }

  @Override
  public int getSize() {
    return super.getName().length() + array.length;
  }

  @Override
  public boolean isDirectory() {
    return false;
  }

  @Override
  public boolean isDataFile() {
    return true;
  }

  @Override
  public DocDirectory asDirectory() {
    throw new UnsupportedOperationException();
  }

  @Override
  public DocDataFile asDataFile() {
    return this;
  }

  @Override
  public DocFile duplicate() {
    return new DocDataFile(super.getName(), array);
  }

  public boolean containsByte(byte b) {
    for (byte arrayElem : array) {
      if (b == arrayElem) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DocDataFile)) {
      return false;
    }
    DocDataFile that = (DocDataFile) o;
    return Arrays.equals(array, that.array) && super.getName().equals(that.getName());
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(array);
  }
}
