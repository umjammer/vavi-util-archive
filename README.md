[![](https://jitpack.io/v/umjammer/vavi-util-archive.svg)](https://jitpack.io/#umjammer/vavi-util-archive)
![Java CI with Maven](https://github.com/umjammer/vavi-util-archive-sandbox/workflows/Java%20CI%20with%20Maven/badge.svg)
![CodeQL](https://github.com/umjammer/vavi-util-archive/workflows/CodeQL/badge.svg)
![Java](https://img.shields.io/badge/Java-8-b07219)

# vavi-util-archive

🌏 Extract the world!

## Status

|name | mathod | read | write | comment | library |
|-----|--------|--------|--------|---------|---------|
|binhex | archiving | 🚧 | - | | [binhex](https://github.com/umjammer/JBinHex) |
|bzip2 | archiving | 🚧 | - | ||
|cab | archiving | 🚧 | - | | [ibex](http://util.ibex.org/src/org/ibex/util/) |
|cab | archiving | 🚧 | - | | [dorkbox](https://github.com/umjammer/CabParser) |
|gca | archiving | | - | ||
|gzip | archiving | ✅ | - | ||
|lha | archiving | ✅ | - | | [lha](https://github.com/umjammer/jlha) |
|rar | archiving | 🚧 | - | ||
|sevenzip | archiving | | - | ||
|stuffit | archiving | | - | ||
|tar | archiving | 🚧 | - | ||
|zip | archiving | ✅ | - | | jdk |
|zip | archiving | ✅ | - | | [ant]() |
|cpio | compression | 🚧 | - | ||
|lzma | compression | 🚧 | - | ||
|rpm | archiving | 🚧 | - | ||

## Library

 * [Giant Java Tree/cpio](https://www.gjt.org/servlets/JCVSlet/list/gjt/org/gjt/archive/cpio) ... Unknown
 * [Giant Java Tree/rpm](https://www.gjt.org/servlets/JCVSlet/list/gjt/org/gjt/archive/rpm) ... Unknown
 * [p7zip/lzma](https://p7zip.sourceforge.net/) ... LGPL
 * [LHA for Java](http://homepage1.nifty.com/dangan/) ... [LICENSE](src/main/java/vavi/util/archive/lha/LISENCE-LHAforJava)

## TODO

 * [commons-vfs](https://commons.apache.org/proper/commons-vfs/)
 * [truevfs](https://github.com/christian-schlichtherle/truevfs)
 * ~~apache commons-compress~~
 * https://github.com/ZIPmagic/ZIPmagic/tree/master/StuffIt%20SDK
 * https://github.com/cstroe/SevenZip-Java