package filesystems;

import java.util.HashSet;
import java.util.Set;

public class DocDirectory extends DocFile {

  private Set<DocFile> containedFiles;

  public DocDirectory(String name, Set<DocFile> containedFiles) {
    super(name);
    this.containedFiles = containedFiles;
  }

  public DocDirectory(String name) {
    this(name, new HashSet<>());
  }

  @Override
  public int getSize() {
    return super.getName().length();
  }

  @Override
  public boolean isDirectory() {
    return true;
  }

  @Override
  public boolean isDataFile() {
    return false;
  }

  @Override
  public DocDirectory asDirectory() {
    return this;
  }

  @Override
  public DocDataFile asDataFile() {
    throw new UnsupportedOperationException();
  }

  @Override
  public DocFile duplicate() {
    Set<DocFile> copyOfContainedFiles = new HashSet<>();
    for (DocFile d : containedFiles) {
      copyOfContainedFiles.add(d.duplicate());
    }
    return new DocDirectory(super.getName(), copyOfContainedFiles);
  }

  public boolean containsFile(String name) {
    for (DocFile d : containedFiles) {
      if (d.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  public Set<DocFile> getAllFiles() {
    return containedFiles;
  }

  public Set<DocDirectory> getDirectories() {
    Set<DocDirectory> returnSet = new HashSet<>();
    for (DocFile d : containedFiles) {
      if (d.isDirectory()) {
        returnSet.add((DocDirectory) d);
      }
    }
    return returnSet;
  }

  public Set<DocDataFile> getDataFiles() {
    Set<DocDataFile> returnSet = new HashSet<>();
    for (DocFile d : containedFiles) {
      if (d.isDataFile()) {
        returnSet.add((DocDataFile) d);
      }
    }
    return returnSet;
  }

  public void addFile(DocFile file) {
    if (this.containsFile(file.getName())) {
      throw new IllegalArgumentException();
    }
    containedFiles.add(file);
  }

  public boolean removeFile(String filename) {
    boolean fileIsInDirectory = this.containsFile(filename);
    if (fileIsInDirectory) {
      containedFiles.removeIf(d -> d.getName().equals(filename));
    }
    return fileIsInDirectory;
  }

  public DocFile getFile(String filename) {
    DocFile returnFile = null;
    for (DocFile d : containedFiles) {
      if (d.getName().equals(filename)) {
        returnFile = d;
      }
    }
    return returnFile;
  }
}


