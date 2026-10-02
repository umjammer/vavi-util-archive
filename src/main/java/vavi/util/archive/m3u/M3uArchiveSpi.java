/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.m3u;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * The service provider for M3U / M3U8 archive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2026-09-22 nsano initial version <br>
 */
public class M3uArchiveSpi implements ArchiveSpi {

    public static final String ENV_KEY_BASE_PATH = "basePath";

    /** the charset of a playlist that is not UTF-8, a {@link Charset} or its name */
    public static final String ENV_KEY_CHARSET = "charset";

    @Override
    public boolean canExtractInput(Object target) throws IOException {
        if (!isSupported(target)) {
            return false;
        }

        InputStream is = null;
        boolean needToClose = false;

        if (target instanceof File) {
            is = new BufferedInputStream(Files.newInputStream(((File) target).toPath()));
            needToClose = true;
        } else if (target instanceof InputStream) {
            is = (InputStream) target;
            if (!is.markSupported()) {
                throw new IllegalArgumentException("InputStream should support #mark()");
            }
        } else {
            assert false : target.getClass().getName();
        }

        byte[] b = new byte[256];
        is.mark(256);
        int l = 0;
        try {
            while (l < 256) {
                int r = is.read(b, l, 256 - l);
                if (r < 0) {
                    break;
                }
                l += r;
            }
        } finally {
            is.reset();
            if (needToClose) {
                is.close();
            }
        }

        if (l < 7) {
            return false;
        }

        String head = new String(b, 0, l, StandardCharsets.UTF_8);
        if (head.startsWith("\uFEFF")) {
            head = head.substring(1);
        }
        return head.stripLeading().startsWith("#EXTM3U");
    }

    @Override
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        Path basePath = null;
        if (env.containsKey(ENV_KEY_BASE_PATH)) {
            Object val = env.get(ENV_KEY_BASE_PATH);
            if (val instanceof Path) {
                basePath = (Path) val;
            } else if (val instanceof File) {
                basePath = ((File) val).toPath();
            } else if (val instanceof String) {
                basePath = Path.of((String) val);
            }
        }

        Charset charset = null;
        if (env.containsKey(ENV_KEY_CHARSET)) {
            Object val = env.get(ENV_KEY_CHARSET);
            if (val instanceof Charset) {
                charset = (Charset) val;
            } else if (val instanceof String) {
                charset = Charset.forName((String) val);
            }
        }

        if (obj instanceof File) {
            return new M3uArchive((File) obj, basePath, charset);
        } else if (obj instanceof InputStream) {
            return new M3uArchive((InputStream) obj, basePath, charset);
        } else {
            throw new IllegalArgumentException("not supported type " + obj.getClass().getName());
        }
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class, InputStream.class};
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] {"m3u", "m3u8", "M3U", "M3U8"};
    }
}
