package vavi.util.archive;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vavi.util.Debug;
import vavi.util.archive.zip.JdkZipArchive;
import vavi.util.archive.zip.JdkZipArchiveSpi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
                arguments("zip", JdkZipArchive.class),
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

    @ParameterizedTest
    @MethodSource("archiveProvider")
    void test11(String extension, Class<Archive> archiveClass) throws Exception {
        Path path = Paths.get("src/test/resources/test." + extension);
        Archive archive = Archives.getArchive(new BufferedInputStream(Files.newInputStream(path)));
        assertEquals(archiveClass, archive.getClass());
    }

    @Test
    void test4() throws Exception {
        assertTrue(new JdkZipArchiveSpi().isSupported(new BufferedInputStream(System.in)));
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

    @Test
    void test3() throws Exception {
        String[] suffixes = Archives.getReaderFileSuffixes();
Debug.println("suffixes: " + Arrays.toString(suffixes));
        assertTrue(Arrays.asList(suffixes).contains("zip"));
        assertFalse(Arrays.asList(suffixes).contains("Z")); // TODO just check the logic, not semantics (means .Z may be included by some compression type)
    }

    @Test
    @DisplayName("when no suitable spi is found, available should not be changed")
    void test5() throws Exception {
        Path file = Path.of("src/test/resources/test.gca");
        InputStream in = new BufferedInputStream(Files.newInputStream(file));
        int before = in.available();
        InputStream is = Archives.getInputStream(in);
        int after = is.available();
Debug.println("result: " + is.getClass().getName());
        assertEquals(before, after);
    }
}
