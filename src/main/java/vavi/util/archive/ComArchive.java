/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import vavi.util.Debug;
import vavi.util.archive.spi.CommonEntry;
import vavix.util.ComUtil;


/**
 * KBA front end．
 * 
 * @target 1.1
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 030211 nsano initial version <br>
 */
public abstract class ComArchive implements Archive {

    /** */
    public static final String TYPE_LHA = "UNLHA";
    /** */
    public static final String TYPE_FTP = "FTP";
    /** */
    public static final String TYPE_TAR = "TAR";
    /** */
    public static final String TYPE_CAB = "CAB";
    /** */
    public static final String TYPE_ZIP = "ZIP";
    /** */
    public static final String TYPE_ARJ = "UNARJ";
    /** */
    public static final String TYPE_RAR = "UNRAR";
    /** */
    public static final String TYPE_UNZIP = "UNZIP";
    /** */
    public static final String TYPE_ISH = "ISH";

    /** */
    private String type;

    /** */
    private List<Entry> entries = new ArrayList<>();

    /** KBA manager */
    private Dispatch manager;
    /** archive COM */
    private Dispatch module;

    /** */
    protected File file;

    /**
     * @throws IOException
     */
    public ComArchive(File file, String type) throws IOException {

        this.file = file;
        this.type = type;
Debug.println("type: " + this.type);
        ComThread.InitSTA();

        // manager
        ActiveXComponent activex = new ActiveXComponent("KBA.Manager");
        manager = activex.getObject();

Debug.println("version: " + Dispatch.get(manager, "Ver"));
Debug.println("unlha: " + Dispatch.get(manager, "UnlhaOk").getBoolean());
Debug.println("ftp: "   + Dispatch.get(manager, "FtpOk").getBoolean());
Debug.println("cab: "   + Dispatch.get(manager, "CabOk").getBoolean());
Debug.println("zip: "   + Dispatch.get(manager, "ZipOk").getBoolean());
Debug.println("unarj: " + Dispatch.get(manager, "UnarjOk").getBoolean());
Debug.println("unrar: " + Dispatch.get(manager, "UnrarOk").getBoolean());
Debug.println("unzip: " + Dispatch.get(manager, "UnzipOk").getBoolean());
Debug.println("ish: "   + Dispatch.get(manager, "IshOk").getBoolean());

        Variant result = Dispatch.invoke(manager, "ArcClass", Dispatch.Method, new Object[] { type }, new int[1]);
Debug.println("arcClass: " + ComUtil.toObject(result));

    	// each module
    	activex = new ActiveXComponent("KBA." + type);
Debug.println("activex: " + "KBA." + type);
        module = activex.getObject();

if ("ZIP".equals(type)) {
 Debug.println("version: " + Dispatch.get(module, "VerUnZip"));
} else {
 Debug.println("version: " + Dispatch.get(module, "Ver"));
}
Debug.println("interval: " + Dispatch.get(module, "CursorInterval"));
Debug.println("background: " + Dispatch.get(module, "BackgroundMode"));
Debug.println("cursorMode: " + Dispatch.get(module, "CursorMode"));
Debug.println("running: " + Dispatch.get(module, "Running"));
        result = Dispatch.invoke(module, "OpenArc", Dispatch.Method, new Object[] { file.toString() }, new int[1]);
Debug.println("openArc: " + ComUtil.toObject(result));
        if (result.getInt() == 0) {
            throw new FileNotFoundException(file.toString());
        }

    	result = Dispatch.invoke(module, "Find", Dispatch.Method, new Object[] { "*" }, new int[1]);
    	if (!result.getBoolean()) {
Debug.println("no content");
            return;
    	}
	
    	do {
            CommonEntry entry = new CommonEntry();

            Variant value = Dispatch.get(module, "FileName");
Debug.println("name: " + value.getClass() + ": " + ComUtil.toObject(value));
            entry.setName(value.getString());

            value = Dispatch.get(module, "FileTime");
Debug.println("time: " + value.getClass() + ": " + ComUtil.toObject(value));
            entry.setTime(value.getJavaDate().getTime());

            value = Dispatch.get(module, "FileAttr");
Debug.println("attr: " + value.getClass() + ": " + ComUtil.toObject(value));

            value = Dispatch.get(module, "FileMode");
Debug.println("mode: " + value.getClass() + ": " + ComUtil.toObject(value));

            value = Dispatch.get(module, "OriginalSize");
Debug.println("size: " + value.getClass() + ": " + ComUtil.toObject(value));
            entry.setSize(value.getInt());

            value = Dispatch.get(module, "CompressedSize");
Debug.println("compressed: " + value.getClass() + ": " + ComUtil.toObject(value));
            entry.setCompressedSize(value.getInt());

            value = Dispatch.get(module, "Ratio");
Debug.println("ratio: " + value.getClass() + ": " + ComUtil.toObject(value));

            entries.add(entry);
//Debug.println(StringUtil.paramString(entry));

            result = Dispatch.invoke(module, "FindNext", Dispatch.Method, new Object[] {}, new int[1]);
Debug.println("findNext: " + ComUtil.toObject(result));
        } while (!result.getBoolean());
/*
        "ArcDateTime";
        "ArcOriginalSize";
        "ArcCompressedSize";
        "ArcRatio";
*/
    }

    /**
     * ファイルを閉じます。
     */
    public void close() throws IOException {
        Variant result = Dispatch.invoke(module, "CloseArc", Dispatch.Method, new Object[] {}, new int[1]);
Debug.println(ComUtil.toObject(result));
        ComThread.Release();
    }

    /**
     * ファイルエントリの列挙を返します。
     */
    public Entry[] entries() {
        Entry[] entries = new Entry[this.entries.size()];
        this.entries.toArray(entries);
        return entries;
    }

    /**
     * 指定された名前の RAR ファイルエントリを返します。
     * 見つからない場合は null を返します。
     */
    public Entry getEntry(String name) {
        for (Entry entry : entries) {
//Debug.println(entry.getName() + ", " + name);
            if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * 指定された ファイルエントリの内容を読み込むための入力ストリームを
     * 返します。
     */
    public InputStream getInputStream(Entry entry) throws IOException {
        Variant result = Dispatch.invoke(module, "ArcCmd", Dispatch.Method, new Object[] { getCommandString(entry) }, new int[1]);
Debug.println("ArcCmd: " + ComUtil.toObject(result));
        String resultString = Dispatch.get(module, "ArcCmdRes").getString();
Debug.println("result: " + resultString);
        if (result.getInt() != 0) {
            throw new IOException(resultString);
        }
        
        String temporaryFileName = getTemporaryFileName(entry);
        File temporaryFile = new File(temporaryFileName);
        if (temporaryFile.exists()) {
            return new BufferedInputStream(new FileInputStream(temporaryFile));
        } else {
            throw new IOException("cannot extract: " + temporaryFileName);
        }
    }

    /** COM に渡すコマンドラインを返してください。 */
    protected abstract String getCommandString(Entry entry);

    /** 解凍されたファイルネームを返してください。 */
    protected abstract String getTemporaryFileName(Entry entry);

    /**
     * ファイルのパス名を返します。
     */
    public String getName() {
        return file.getPath();
    }

    /**
     * ファイル中のエントリの数を返します。
     */
    public int size() {
        return entries.size();
    }
}

/* */
