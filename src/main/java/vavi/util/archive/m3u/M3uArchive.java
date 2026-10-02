/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.m3u;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.lindstrom.m3u8.model.AlternativeRendition;
import io.lindstrom.m3u8.model.ByteRange;
import io.lindstrom.m3u8.model.IFrameVariant;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.MediaSegment;
import io.lindstrom.m3u8.model.MultivariantPlaylist;
import io.lindstrom.m3u8.model.Playlist;
import io.lindstrom.m3u8.model.Variant;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import io.lindstrom.m3u8.parser.MultivariantPlaylistParser;
import io.lindstrom.m3u8.parser.ParsingMode;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.InputStreamSupport;

import static java.lang.System.getLogger;


/**
 * M3uArchive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2026-09-22 nsano initial version <br>
 */
public class M3uArchive extends InputStreamSupport implements Archive {

    private static final Logger logger = getLogger(M3uArchive.class.getName());

    private final File file;
    private final Path basePath;
    /** used when the playlist is not UTF-8 */
    private final Charset charset;
    private Playlist playlist;
    private Entry[] entries;

    public M3uArchive(File file) throws IOException {
        this(file, (Path) null);
    }

    public M3uArchive(File file, Path basePath) throws IOException {
        this(file, basePath, null);
    }

    /**
     * @param charset used when the playlist is not UTF-8, null means the platform default
     */
    public M3uArchive(File file, Path basePath, Charset charset) throws IOException {
        this.file = file;
        this.basePath = basePath != null ? basePath : (file != null ? file.toPath().toAbsolutePath().getParent() : null);
        this.charset = charset != null ? charset : Charset.defaultCharset();
        init(readLines(file.toPath()));
    }

    public M3uArchive(InputStream is) throws IOException {
        this(is, (Path) null);
    }

    public M3uArchive(InputStream is, Path basePath) throws IOException {
        this(is, basePath, null);
    }

    /**
     * @param charset used when the playlist is not UTF-8, null means the platform default
     */
    public M3uArchive(InputStream is, Path basePath, Charset charset) throws IOException {
        super(is);
        this.file = this.archiveFileForInputStream;
        this.basePath = basePath;
        this.charset = charset != null ? charset : Charset.defaultCharset();
        init(readLines(this.file.toPath()));
    }

    private List<String> readLines(Path path) throws IOException {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            return Files.readAllLines(path, charset);
        }
    }

    private void init(List<String> rawLines) throws IOException {
        List<String> lines = new ArrayList<>(rawLines);
        if (!lines.isEmpty() && lines.get(0).startsWith("\uFEFF")) {
            lines.set(0, lines.get(0).substring(1));
        }

        boolean isMultivariant = lines.stream().anyMatch(l -> l.startsWith("#EXT-X-STREAM-INF") || l.startsWith("#EXT-X-I-FRAME-STREAM-INF"));
        if (isMultivariant) {
            MultivariantPlaylistParser parser = new MultivariantPlaylistParser(ParsingMode.LENIENT);
            this.playlist = parser.readPlaylist(lines.iterator());
            List<Entry> entryList = new ArrayList<>();
            MultivariantPlaylist mv = (MultivariantPlaylist) this.playlist;
            for (Variant v : mv.variants()) {
                MediaSegment.Builder b = MediaSegment.builder()
                        .uri(v.uri())
                        .duration(0)
                        .bitrate(v.bandwidth());
                v.resolution().ifPresent(r -> b.title(r.width() + "x" + r.height()));
                entryList.add(new M3uEntry(b.build()));
            }
            for (IFrameVariant v : mv.iFrameVariants()) {
                MediaSegment s = MediaSegment.builder()
                        .uri(v.uri())
                        .duration(0)
                        .bitrate(v.bandwidth())
                        .build();
                entryList.add(new M3uEntry(s));
            }
            for (AlternativeRendition a : mv.alternativeRenditions()) {
                a.uri().ifPresent(u -> {
                    MediaSegment s = MediaSegment.builder()
                            .uri(u)
                            .duration(0)
                            .title(a.name())
                            .build();
                    entryList.add(new M3uEntry(s));
                });
            }
            this.entries = entryList.toArray(new Entry[0]);
        } else {
            List<String> transformed = transform(lines);
            MediaPlaylistParser parser = new MediaPlaylistParser(ParsingMode.LENIENT);
            this.playlist = parser.readPlaylist(transformed.iterator());
            MediaPlaylist mp = (MediaPlaylist) this.playlist;
            this.entries = mp.mediaSegments().stream().map(M3uEntry::new).toArray(Entry[]::new);
        }
    }

    private static List<String> transform(List<String> lines) {
        boolean hasTargetDuration = lines.stream().anyMatch(l -> l.startsWith("#EXT-X-TARGETDURATION"));
        List<String> result = new ArrayList<>();
        boolean lastWasExtInf = false;
        boolean extm3uAdded = false;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (trimmed.equals("#EXTM3U")) {
                result.add(trimmed);
                extm3uAdded = true;
                if (!hasTargetDuration) {
                    result.add("#EXT-X-TARGETDURATION:0");
                }
                continue;
            }
            if (trimmed.startsWith("#EXTINF")) {
                lastWasExtInf = true;
                if (trimmed.startsWith("#EXTINF:")) {
                    int comma = trimmed.indexOf(',');
                    String durPart = comma > 0 ? trimmed.substring(8, comma).trim() : trimmed.substring(8).trim();
                    String[] parts = durPart.split("\\s+");
                    String normDur = parts[0];
                    try {
                        Double.parseDouble(normDur);
                    } catch (NumberFormatException e) {
                        normDur = "-1";
                    }
                    String title = comma > 0 ? trimmed.substring(comma + 1) : "";
                    result.add("#EXTINF:" + normDur + "," + title);
                } else {
                    result.add(trimmed);
                }
                continue;
            }
            if (trimmed.startsWith("#")) {
                result.add(trimmed);
                continue;
            }
            // URI line
            if (!lastWasExtInf) {
                result.add("#EXTINF:-1,");
            }
            result.add(trimmed);
            lastWasExtInf = false;
        }

        if (!extm3uAdded) {
            result.add(0, "#EXTM3U");
            if (!hasTargetDuration) {
                result.add(1, "#EXT-X-TARGETDURATION:0");
            }
        }

        return result;
    }

    @Override
    public Entry[] entries() {
        return entries != null ? entries : new Entry[0];
    }

    @Override
    public Entry getEntry(String name) {
        return Arrays.stream(entries()).filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        if (entry == null) {
            throw new IllegalArgumentException("entry is null");
        }
        MediaSegment segment = ((M3uEntry) entry).getWrappedObject();
        String uri = segment.uri();

        InputStream is;
        if (uri.startsWith("http://") || uri.startsWith("https://") || uri.startsWith("ftp://")) {
            is = URI.create(uri).toURL().openStream();
        } else if (uri.startsWith("file:")) {
            Path p = Paths.get(URI.create(uri));
            is = Files.newInputStream(p);
        } else {
            Path p = Paths.get(uri);
            if (!p.isAbsolute()) {
                Path base = basePath != null ? basePath : (file != null ? file.toPath().toAbsolutePath().getParent() : null);
                if (base != null) {
                    p = base.resolve(p);
                }
            }
            if (!Files.exists(p)) {
                try {
                    String decoded = URLDecoder.decode(uri, StandardCharsets.UTF_8);
                    Path decodedPath = Paths.get(decoded);
                    if (!decodedPath.isAbsolute()) {
                        Path base = basePath != null ? basePath : (file != null ? file.toPath().toAbsolutePath().getParent() : null);
                        if (base != null) {
                            decodedPath = base.resolve(decodedPath);
                        }
                    }
                    if (Files.exists(decodedPath)) {
                        p = decodedPath;
                    }
                } catch (Exception ignored) {
                }
            }
            is = Files.newInputStream(p);
        }

        if (segment.byteRange().isPresent()) {
            ByteRange byteRange = segment.byteRange().get();
            long offset = byteRange.offset().orElse(0L);
            long length = byteRange.length();
            if (offset > 0) {
                is.skipNBytes(offset);
            }
            return new BoundedInputStream(is, length);
        }

        return is;
    }

    @Override
    public String getName() {
        return file != null ? file.getPath() : "";
    }

    @Override
    public int size() {
        return entries().length;
    }

    @Override
    public void close() throws IOException {
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    private static class BoundedInputStream extends FilterInputStream {
        private long remaining;

        BoundedInputStream(InputStream in, long max) {
            super(in);
            this.remaining = max;
        }

        @Override
        public int read() throws IOException {
            if (remaining <= 0) {
                return -1;
            }
            int r = super.read();
            if (r >= 0) {
                remaining--;
            }
            return r;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (remaining <= 0) {
                return -1;
            }
            int toRead = (int) Math.min(len, remaining);
            int r = super.read(b, off, toRead);
            if (r > 0) {
                remaining -= r;
            }
            return r;
        }

        @Override
        public int available() throws IOException {
            return (int) Math.min(super.available(), remaining);
        }
    }
}
