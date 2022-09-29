[![](https://jitpack.io/v/umjammer/vavi-util-archive.svg)](https://jitpack.io/#umjammer/vavi-util-archive)
[![Java CI with Maven](https://github.com/umjammer/vavi-util-archive-sandbox/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/umjammer/vavi-util-archive-sandbox/actions)
[![CodeQL](https://github.com/umjammer/vavi-util-archive/workflows/CodeQL/badge.svg)](https://github.com/umjammer/vavi-util-archive-sandbox/actions)
![Java](https://img.shields.io/badge/Java-8-b07219)

# vavi-util-archive

🌏 Extract the world!

archives are able to mount as fuse also using [vavi-nio-file-archive](https://github.com/umjammer/vavi-apps-fuse/tree/master/vavi-nio-file-archive)
and [vavi-net-fuse](https://github.com/umjammer/vavi-apps-fuse/tree/master/vavi-net-fuse)

## Status

| name     | mathod    | read   | write | comment | library                                                                 |
|----------|-----------|--------|--------|---------|-------------------------------------------------------------------------|
| binhex   | archiving | ✅     | - |         | [binhex](https://github.com/umjammer/JBinHex)                           |
| bzip2    | archiving | ✅      | - |         | [commons-compress](https://commons.apache.org/proper/commons-compress/) |
| cab      | archiving | ✅      | - |         | [dorkbox](https://github.com/umjammer/CabParser)                        |
| gca      | archiving | -      | - | win only | native                                                                        |
| gca      | archiving | -      | - |         | shell                                                                        |
| gzip     | archiving | ✅      | - |         | jdk                                                                     |
| lha      | archiving | ✅      | - |         | [lha](https://github.com/umjammer/jlha)                                 |
| rar      | archiving | -      | - | win only | native                                                                    |
| rar      | archiving | -      | - |         | shell                                                                     |
| rar      | archiving | ✅      | - |         | [java-unrar](https://github.com/umjammer/java-unrar)                    |
| rar      | archiving | ✅      | - | no rar5 | [junrar](https://github.com/junrar/junrar)                              |
| sevenzip | archiving | ✅     | - | 7z only | [commons-compress](https://commons.apache.org/proper/commons-compress/) |
| sevenzip | archiving | -      | - |         | native                                                                        |
| stuffit  | archiving |        | - |         | native                                                                        |
| tar      | archiving | ✅     | - |         | [commons-compress](https://commons.apache.org/proper/commons-compress/) |
| tar      | stream    | ✅     | - |         | [commons-compress](https://commons.apache.org/proper/commons-compress/) |
| zip      | archiving | ✅      | - |         | jdk                                                                     |
| zip      | archiving | ✅      | - |         | [ant](https://ant.apache.org/)                                          |
| cpio     | stream    | 🚧     | - |         | gjt                                                                     |
| lzma     | stream    | 🚧     | - |         | [p7zip](https://p7zip.sourceforge.net/)                                 |
| rpm      | archiving | 🚧     | - |         | gjt                                                                     |
| apache   | archiving | ✅      | - | multi   | [commons-compress](https://commons.apache.org/proper/commons-compress/) |

## Library

 * [Giant Java Tree/cpio](http://www.gjt.org/servlets/JCVSlet/list/gjt/org/gjt/archive/cpio) ... Unknown
 * [Giant Java Tree/rpm](http://www.gjt.org/servlets/JCVSlet/list/gjt/org/gjt/archive/rpm) ... Unknown
 * [p7zip/lzma](https://p7zip.sourceforge.net/) ... LGPL
 * [LHA for Java](http://homepage1.nifty.com/dangan/) ... [LICENSE](src/main/java/vavi/util/archive/lha/LISENCE-LHAforJava)

## TODO

 * [commons-vfs](https://commons.apache.org/proper/commons-vfs/)
 * [truevfs](https://github.com/christian-schlichtherle/truevfs)
 * ~~apache commons-compress~~
 * https://github.com/ZIPmagic/ZIPmagic/tree/master/StuffIt%20SDK
 * https://github.com/cstroe/SevenZip-Java
 * write!
 * jar5
   * ~~[@marcusvoltolim]((https://github.com/marcusvoltolim/file-processor#readme)) says [sevenzipjbinding](https://github.com/borisbrodski/sevenzipjbinding) can deal rar5 ???~~
     * it's [true](https://github.com/borisbrodski/sevenzipjbinding/issues/19#issuecomment-578636772)
   * unar v1.10.7 ... ok
   * 7zip 17.04 ... ok
 * https://github.com/prog-ai/ArchivR
 * https://github.com/Diab1o/java-7z-archiver ... yet another pure java 7zip implementation?
 * where is my jna 7z implementation? (in the crashed hdd?)