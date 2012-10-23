/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import vavi.util.Debug;
import vavi.util.archive.ComArchive;
import vavi.util.archive.Entry;


/**
 * CAB アーカイブを処理するサービスプロバイダです．
 * (COM バージョン)
 * 
 * @target 1.1
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 041002 nsano initial version <br>
 */
public class ComCabArchive extends ComArchive {
    
    /** */
    public ComCabArchive(File file) throws IOException {
        super(file, TYPE_CAB);
    }
    
    /** */
    private static final MessageFormat commandLineBase = new MessageFormat("-x -i -o \"{0}\" \"{1}\" \"{2}\"");

    /** */
    protected String getCommandString(Entry entry) {

        String commandLine = commandLineBase.format(new Object[] {
            file.getPath(),
            System.getProperty("java.io.tmpdir"),
            entry.getName()
        });
Debug.println("commandLine: " + commandLine);

        return commandLine;
    }

    /** */
    protected String getTemporaryFileName(Entry entry) {
        return System.getProperty("java.io.tmpdir") + entry.getName();
    }

    //----
    
    /** */
    public static void main(String[] args) throws Exception {
        ComCabArchive rar = new ComCabArchive(new File(args[0]));
        Entry entry = rar.getEntry(args[1]);
System.err.println("entry: " + entry);
        InputStream is = rar.getInputStream(entry);
System.err.println("is: " + is);
    }
}

/* */
