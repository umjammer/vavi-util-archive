package vavi.util.archive;

import java.io.File;
import java.io.InputStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;


/**
 * ArchivesTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-06-20 nsano initial version <br>
 */
class ArchivesTest {

    static Stream<Arguments> archiveProvider() {
        return Stream.of(
                arguments("zip", vavi.util.archive.zip.ZipArchive.class),
                arguments("cab", vavi.util.archive.cab.DorkboxCabArchive.class),
                arguments("7z", vavi.util.archive.sevenzip.ApacheSevenZipArchive.class),
                arguments("rar", vavi.util.archive.rar.PureJavaRarArchive.class),
                arguments("lzh", vavi.util.archive.lha.LhaArchive.class)
        );
    }

    @ParameterizedTest
    @MethodSource("archiveProvider")
    void test1(String extension, Class<Archive> archiveClass) throws Exception {
        File file = new File("src/test/resources/test." + extension);
        Archive archive = Archives.getArchive(file);
        assertEquals(archiveClass, archive.getClass());
    }

    static Stream<Arguments> streamProvider() {
        return Stream.of(
                arguments("sit.hqx", org.gjt.convert.binhex.BinHex4InputStream.class),
                arguments("tar.gz", java.util.zip.GZIPInputStream.class),
                arguments("tar.bz2", org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream.class),
                arguments("tar", org.apache.commons.compress.archivers.tar.TarArchiveInputStream.class)
        );
    }

    @ParameterizedTest
    @MethodSource("streamProvider")
    void test2(String extension, Class<Archive> archiveClass) throws Exception {
        File file = new File("src/test/resources/test." + extension);
        InputStream is = Archives.getInputStream(file);
        assertEquals(archiveClass, is.getClass());
    }
}
