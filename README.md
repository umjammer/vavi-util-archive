[![](https://jitpack.io/v/umjammer/vavi-util-archive.svg)](https://jitpack.io/#umjammer/vavi-util-archive)
[![Java CI](https://github.com/umjammer/vavi-util-archive/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-util-archive/actions/workflows/maven.yml)
[![CodeQL](https://github.com/umjammer/vavi-util-archive/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-util-archive/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-17-b07219)

# vavi-util-archive

🌏 Extract the world!

extract all archive types in the same way!</br>
archives are able to mount as fuse also using [vavi-nio-file-archive](https://github.com/umjammer/vavi-apps-fuse/tree/master/vavi-nio-file-archive)
and [vavi-net-fuse](https://github.com/umjammer/vavi-apps-fuse/tree/master/vavi-net-fuse)

### Status

| name     | mathod    | read | write | comment | library                                                                 |
|----------|-----------|------|--------|---------|-------------------------------------------------------------------------|
| binhex   | archiving | ✅    | - |         | [binhex](https://github.com/umjammer/JBinHex)                           |
| bzip2    | archiving | ✅    | - |         | [commons-compress](https://commons.apache.org/proper/commons-compress/) |
| cab      | archiving | ✅    | - |         | [dorkbox](https://github.com/umjammer/CabParser)                        |
| gca      | archiving | -    | - | win only | native                                                                        |
| gca      | archiving | -    | - |         | shell                                                                        |
| gzip     | archiving | ✅    | - |         | jdk                                                                     |
| lha      | archiving | ✅    | - |         | [lha](https://github.com/umjammer/jlha)                                 |
| rar      | archiving | -    | - | win only | native                                                                    |
| rar      | archiving | -    | - |         | shell                                                                     |
| rar      | archiving | ✅*   | - |         | [java-unrar](https://github.com/umjammer/java-unrar)                    |
| rar      | archiving | ✅    | - | no rar5 | [junrar](https://github.com/junrar/junrar)                              |
| sevenzip | archiving | ✅    | - | 7z only | [commons-compress](https://commons.apache.org/proper/commons-compress/) |
| sevenzip | archiving | -    | - |         | native                                                                        |
| stuffit  | archiving |      | - |         | native                                                                        |
| tar      | archiving | ✅    | - |         | [commons-compress](https://commons.apache.org/proper/commons-compress/) |
| tar      | stream    | ✅    | - |         | [commons-compress](https://commons.apache.org/proper/commons-compress/) |
| zip      | archiving | ✅*   | - |         | jdk                                                                     |
| zip      | archiving | ✅    | - |         | [ant](https://ant.apache.org/)                                          |
| cpio     | stream    | 🚧   | - |         | gjt                                                                     |
| lzma     | stream    | 🚧   | - |         | [p7zip](https://p7zip.sourceforge.net/)                                 |
| rpm      | archiving | 🚧   | - |         | gjt                                                                     |
| apache   | archiving | ✅    | - | multi   | [commons-compress](https://commons.apache.org/proper/commons-compress/) |

<sub>* chosen as spi</sub>

## Install

 * [maven](https://jitpack.io/#umjammer/vavi-util-archive)

## Usage

### archive extraction

```java
    Archive archive = Archives.getArchive(Paths.get("foo/bar.rar").toFile());
    Path outDir = Paths.get("foo/bar");
    for (Entry entry : archive.entries()) {
        Files.copy(archive.getInputStream(entry), outDir.resolve(entry.getName()));
    }
```

### archive decompression

```java
    InputStream compressed = Archives.getInputStream(Paths.get("foo/bar.tar.bz").toFile());
    Files.copy(compressed, Paths.get("foo/bar.tar"));
```
## References

### License

 * [Giant Java Tree/cpio](http://www.gjt.org/servlets/JCVSlet/list/gjt/org/gjt/archive/cpio) ... Unknown
 * [Giant Java Tree/rpm](http://www.gjt.org/servlets/JCVSlet/list/gjt/org/gjt/archive/rpm) ... Unknown
 * [p7zip/lzma](https://p7zip.sourceforge.net/) ... LGPL
 * [LHA for Java](http://homepage1.nifty.com/dangan/) ... [LICENSE](src/main/java/vavi/util/archive/lha/LISENCE-LHAforJava)

## TODO

 * registry like IIORegistry
 * [commons-vfs](https://commons.apache.org/proper/commons-vfs/)
 * [truevfs](https://github.com/christian-schlichtherle/truevfs)
 * ~~apache commons-compress~~
 * https://github.com/ZIPmagic/ZIPmagic/tree/master/StuffIt%20SDK
 * ~~https://github.com/cstroe/SevenZip-Java~~ (use 7zip-jbinding)
 * write!
 * ~~rar5~~ (done by 7zip-jbinding)
   * ~~[@marcusvoltolim]((https://github.com/marcusvoltolim/file-processor#readme)) says [sevenzipjbinding](https://github.com/borisbrodski/sevenzipjbinding) can deal rar5 ???~~
     * it's [true](https://github.com/borisbrodski/sevenzipjbinding/issues/19#issuecomment-578636772)
   * unar v1.10.7 ... ok
   * 7zip 17.04 ... ok
 * ~~https://github.com/Diab1o/java-7z-archiver ... yet another pure java 7zip implementation?~~ (done)
 * where is my jna 7z implementation? (in the crashed hdd?)
 * binary things ... gca.exe -> dll -> 64bit -> mach-O + winelib -> dylib
   * https://github.com/gitGNU/objconv
   * https://github.com/jakeajames/dylibify
   * https://github.com/hasherezade/exe_to_dll