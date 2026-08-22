/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.gca;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import jdos.api.JDosBox;

import vavi.util.archive.Archive;
import vavi.util.archive.CommonEntry;
import vavi.util.archive.Entry;

import static java.lang.System.getLogger;


/**
 * DosboxGcaArchive represents a GCA archive read by gcac.exe running on a jdosbox machine.
 * <p>
 * GCA was only ever given a win32 program, and {@link NativeGcaArchive} and
 * {@link ShellGcaArchive} both need that program to be run by windows itself. Here the
 * program is run by an emulated PC instead, so a GCA archive can be read wherever java is.
 * <p>
 * The archive is copied onto a directory mounted as the machine's C: drive, and the guest is
 * asked for its listing and then for its contents. {@code gcac} has no way to extract one
 * entry on its own, so the whole archive is extracted at once and served from that directory
 * until this archive is {@link #close closed}.
 * <p>
 * The listing gives the times, which extraction does not preserve, but it abbreviates a name
 * that does not fit its 29 byte column ({@code ...nZipResource_ja.properties}); the extraction
 * writes each name out in full. Each entry therefore takes its name from the extraction and
 * the rest of what it knows from the listing, paired in the order gcac gives both in.
 * <p>
 * system properties
 * <li> "vavi.util.archive.gca.gcac" ({@link #GCAC}) ... where the gcac.exe to run is
 * <li> "vavi.util.archive.gca.memsize" ({@link #MEMSIZE}) ... the machine's memory in MB
 * <li> "vavi.util.archive.gca.timeout" ({@link #TIMEOUT}) ... how long the machine is given, in ms
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 260822 nsano initial version <br>
 */
public class DosboxGcaArchive implements Archive {

    private static final Logger logger = getLogger(DosboxGcaArchive.class.getName());

    /** where the gcac.exe to run is */
    public static final String GCAC = "vavi.util.archive.gca.gcac";

    /** the emulated machine's memory in MB; gcac cannot start in the 16 MB dosbox defaults to */
    public static final String MEMSIZE = "vavi.util.archive.gca.memsize";

    /** how long the machine is given to finish, in milliseconds */
    public static final String TIMEOUT = "vavi.util.archive.gca.timeout";

    /** */
    public static final String DEFAULT_GCAC = "/usr/local/src/GCA/gcac.exe";

    /** */
    private static final String DEFAULT_MEMSIZE = "32";

    /** */
    private static final String DEFAULT_TIMEOUT = "120000";

    /** what gcac 0.9k writes, and what it reads a japanese filename as */
    private static final Charset encoding = Charset.forName("windows-31j");

    /** the program is called this on the mounted drive, whatever it is called here */
    private static final String GCAC_EXE = "GCAC.EXE";

    /** and the archive this, so that the guest never sees a name dos cannot */
    private static final String ARCHIVE = "ARC.GCA";

    /** and is extracted into this, under the same drive */
    private static final String EXTRACTED = "out";

    /** what a line of the extraction output says before the file it wrote */
    private static final String EXTRACTING = "extracting ";

    /** the whole of an extracted path as the guest saw it */
    private static final String EXTRACTED_PREFIX = "C:\\" + EXTRACTED + "\\";

    /** */
    private static final Lock lock = new ReentrantLock();

    /** */
    private final File file;

    /** the directory mounted as the machine's C: drive, holding gcac, the archive and its contents */
    private final Path work;

    /** */
    private final List<CommonEntry> entries = new ArrayList<>();

    /** entry name -> the file the guest extracted it to */
    private final Map<String, Path> files = new HashMap<>();

    /** */
    public DosboxGcaArchive(File file) throws IOException {

        this.file = file;

        Path gcac = Path.of(System.getProperty(GCAC, DEFAULT_GCAC));
        if (!Files.exists(gcac)) {
            throw new IOException("no gcac.exe to run: " + gcac);
        }

        // everything the guest is given is named for dos rather than for us: whatever the
        // program and the archive are called here, the machine sees eight and three
        work = Files.createTempDirectory("vavi-util-archive-gca-");
        Path extracted = Files.createDirectory(work.resolve(EXTRACTED));
        Files.copy(gcac, work.resolve(GCAC_EXE));
        Files.copy(file.toPath(), work.resolve(ARCHIVE));

        try {
            // one machine each: jdosbox reboots itself when a win32 program returns to the dos
            // prompt, and a machine that has been through that leaves the next one in this jvm
            // with a guest that never starts
            List<byte[]> listing = lines(exec(work, GCAC_EXE + " l " + ARCHIVE));
            List<byte[]> extraction = lines(exec(work, GCAC_EXE + " e " + ARCHIVE + " " + EXTRACTED));

            reconcile(listed(listing), extractedNames(extraction), extracted);
        } catch (IOException | RuntimeException e) {
            // nobody is going to be given this archive to close
            delete(work);
            throw e;
        }
    }

    /**
     * Runs one guest command on a machine of its own and gives back what it wrote.
     * <p>
     * The machine ends with the program rather than going back to the dos prompt, where it
     * would sit for as long as it was let - and where jdosbox reboots it, which is what a
     * later machine in the same jvm does not survive.
     */
    private static byte[] exec(Path work, String command) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // jdosbox keeps its machine in statics, so only one of them may be running at a time
        lock.lock();
        try {
            JDosBox dosbox = new JDosBox()
                    .set("dosbox", "memsize", System.getProperty(MEMSIZE, DEFAULT_MEMSIZE))
                    .mount('c', work.toFile())
                    .command("c:")
                    .command(command)
                    .stdioSink((data, offset, length, frames) -> baos.write(data, offset, length))
                    .exitWhenProgramFinishes(true);

            long timeout = Long.parseLong(System.getProperty(TIMEOUT, DEFAULT_TIMEOUT));
            dosbox.start();
            try {
                if (!dosbox.await(timeout)) {
                    throw new IOException("gcac did not finish in " + timeout + " ms: " + command);
                }
            } finally {
                dosbox.stop();
            }
            if (dosbox.getFailure() != null) {
                throw new IOException(dosbox.getFailure());
            }
        } finally {
            lock.unlock();
        }

        byte[] output = baos.toByteArray();
logger.log(Level.TRACE, "'" + command + "' wrote " + output.length + " bytes:\n" + new String(output, encoding));
        return output;
    }

    /** splits what the guest wrote into its crlf separated lines */
    private static List<byte[]> lines(byte[] output) {
        List<byte[]> lines = new ArrayList<>();
        int start = 0;
        for (int i = 0; i <= output.length; i++) {
            if (i == output.length || output[i] == '\n') {
                int end = i;
                if (end > start && output[end - 1] == '\r') {
                    end--;
                }
                if (end > start || i < output.length) {
                    byte[] line = new byte[end - start];
                    System.arraycopy(output, start, line, 0, line.length);
                    lines.add(line);
                }
                start = i + 1;
            }
        }
        return lines;
    }

    /**
     * Reads the rows of the {@code l} listing.
     * <p>
     * The columns are taken from the rule line gcac draws under its header rather than assumed,
     * and they are counted in bytes, which is how the program pads them - a japanese name is
     * two bytes a character to it. Splitting on whitespace instead would come apart on the first
     * name with a space in it.
     */
    private static List<CommonEntry> listed(List<byte[]> lines) throws IOException {

        List<CommonEntry> listed = new ArrayList<>();

        int rule = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (isRule(lines.get(i))) {
                rule = i;
                break;
            }
        }
        if (rule < 0) {
            // gcac says nothing at all about an archive it cannot open, so a listing with no
            // header to it is the only sign of one - and that one is not this provider's
            throw new IOException("gcac read no listing out of the archive");
        }

        List<int[]> columns = columns(lines.get(rule));
        if (columns.size() < 4) {
            throw new IOException("gcac listed columns this provider does not know: " + columns.size());
        }

        for (int i = rule + 1; i < lines.size(); i++) {
            byte[] line = lines.get(i);
            // every row starts its name hard against the left; anything else - a blank line,
            // or the extraction talking - is the end of the listing
            if (line.length == 0 || line[0] == ' ') {
                break;
            }

            CommonEntry entry = new CommonEntry();
            entry.setName(field(line, columns.get(0)));
            entry.setTime(time(field(line, columns.get(1))));
            entry.setSize(number(field(line, columns.get(2))));
            entry.setCompressedSize(number(field(line, columns.get(3))));
            listed.add(entry);
        }

        return listed;
    }

    /** is this the rule line gcac draws under the listing header? */
    private static boolean isRule(byte[] line) {
        if (line.length == 0 || line[0] != '-') {
            return false;
        }
        for (byte b : line) {
            if (b != '-' && b != ' ') {
                return false;
            }
        }
        return true;
    }

    /** where each column of the listing starts and ends, in bytes */
    private static List<int[]> columns(byte[] rule) {
        List<int[]> columns = new ArrayList<>();
        int start = -1;
        for (int i = 0; i < rule.length; i++) {
            if (rule[i] == '-') {
                if (start < 0) {
                    start = i;
                }
            } else if (start >= 0) {
                columns.add(new int[] {start, i});
                start = -1;
            }
        }
        if (start >= 0) {
            columns.add(new int[] {start, rule.length});
        }
        return columns;
    }

    /** */
    private static String field(byte[] line, int[] column) {
        int start = Math.min(column[0], line.length);
        int end = Math.min(column[1], line.length);
        return new String(line, start, end - start, encoding).trim();
    }

    /** */
    private static long number(String field) {
        try {
            return Long.parseLong(field);
        } catch (NumberFormatException e) {
logger.log(Level.DEBUG, "not a number: " + field);
            return 0;
        }
    }

    /** */
    private static long time(String field) {
        try {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(field).getTime();
        } catch (ParseException e) {
logger.log(Level.DEBUG, "not a date & time: " + field);
            return 0;
        }
    }

    /** the names the extraction wrote out, which unlike the listing's are whole */
    private static List<String> extractedNames(List<byte[]> lines) {
        List<String> names = new ArrayList<>();
        for (byte[] line : lines) {
            String s = new String(line, encoding).trim();
            if (!s.startsWith(EXTRACTING)) {
                continue;
            }
            String path = s.substring(EXTRACTING.length());
            if (path.regionMatches(true, 0, EXTRACTED_PREFIX, 0, EXTRACTED_PREFIX.length())) {
                path = path.substring(EXTRACTED_PREFIX.length());
            }
            names.add(path);
        }
        return names;
    }

    /**
     * Puts each extracted file together with the listing row that describes it.
     * <p>
     * gcac lists and extracts in the same order, so pairing by position is the one way round
     * that needs nothing of the names - which is what makes it right, because the listing is
     * where a long name is abbreviated. Only when the two disagree on how many there are is
     * there anything to work out, and then the names are all there is to go on.
     */
    private void reconcile(List<CommonEntry> listed, List<String> extractedNames, Path extracted) throws IOException {

        if (listed.size() == extractedNames.size()) {
            for (int i = 0; i < listed.size(); i++) {
                add(listed.get(i), extractedNames.get(i), extracted);
            }
            return;
        }

logger.log(Level.WARNING, "gcac listed " + listed.size() + " entries but extracted " + extractedNames.size());

        // the listing is what the archive holds, so every row of it becomes an entry either
        // way; a row no extracted file could be found for keeps the name the listing gave it
        // and has nothing to read
        List<String> rest = new LinkedList<>(extractedNames);
        for (CommonEntry entry : listed) {
            String name = null;
            for (String candidate : rest) {
                if (matches(entry.getName(), candidate)) {
                    name = candidate;
                    break;
                }
            }
            if (name != null) {
                rest.remove(name);
            }
            add(entry, name, extracted);
        }
    }

    /** does a name the listing abbreviated stand for this whole one? */
    private static boolean matches(String listed, String extracted) {
        if (listed == null) {
            return false;
        }
        if (listed.equals(extracted)) {
            return true;
        }
        // "...nZipResource_ja.properties" is the tail of a name too long for the column
        return listed.startsWith("...") && extracted.endsWith(listed.substring(3));
    }

    /**
     * Adds one entry, under the name the extraction wrote it out as - or, when there was no
     * extracted file to go with it, under the one the listing gave.
     */
    private void add(CommonEntry entry, String extractedName, Path extracted) throws IOException {

        Path path = null;
        if (extractedName != null) {
            path = extracted.resolve(extractedName.replace('\\', File.separatorChar));
            if (!path.normalize().startsWith(extracted)) {
                // the archive named a place outside the directory it was extracted into
                throw new IOException("bad gca entry: " + extractedName);
            }
            entry.setName(extractedName);
        }

        String name = entry.getName().replace('\\', '/');
        entry.setName(name);
        if (path != null) {
            entry.setDirectory(Files.isDirectory(path));
            if (entry.getSize() == 0 && !entry.isDirectory() && Files.exists(path)) {
                entry.setSize(Files.size(path));
            }
            files.put(name, path);
        }
logger.log(Level.DEBUG, "entry: " + name + ", " + entry.getSize() + ", " + new Date(entry.getTime()));

        entries.add(entry);
    }

    @Override
    public void close() throws IOException {
        // what the guest extracted only ever lived for as long as this archive did
        delete(work);
    }

    /** */
    private static void delete(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(directory)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    @Override
    public Entry[] entries() {
        return entries.toArray(new Entry[0]);
    }

    @Override
    public Entry getEntry(String name) {
        for (Entry entry : entries) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        Path path = files.get(entry.getName());
        if (path == null) {
            throw new IOException("no such entry: " + entry.getName());
        }
        if (!Files.exists(path)) {
            throw new IOException("cannot extract: " + entry.getName());
        }
        return new BufferedInputStream(Files.newInputStream(path));
    }

    @Override
    public String getName() {
        return file.getPath();
    }

    @Override
    public int size() {
        return entries.size();
    }
}
