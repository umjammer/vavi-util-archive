package vavi.util.codec.cpio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import vavi.util.codec.cpio.CPIOEntry;
import vavi.util.codec.cpio.CPIOInputStream;


public class cpio {

    private static final int ACT_NONE = 0;
    private static final int ACT_COPYIN = 1;
    private static final int ACT_COPYOUT = 2;
    private static final int ACT_PASSTHRU = 3;

    private int action = ACT_NONE;

    @SuppressWarnings("unused")
    private boolean quiet = false;
    private boolean verbose = false;
    private boolean listMode = false;

    private InputStream inStream = null;

    @SuppressWarnings("unused")
    private OutputStream outStream = null;

    private File extractBase = null;

    public static void main(String[] args) throws Exception {
        cpio app = new cpio();
        app.processArguments(args);
        app.processCPIO();
    }

    // UNDONE put this on top of a GJT command line package!
    private void processArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-i")) {
                this.action = ACT_COPYIN;

                if (this.inStream == null)
                    this.inStream = System.in;

                if (this.extractBase == null)
                    this.extractBase = // REVIEW the default value here
                    new File(System.getProperty("user.dir", "."));
            } else if (args[i].equals("-o")) {
                this.action = ACT_COPYOUT;
            } else if (args[i].equals("-p")) {
                this.action = ACT_PASSTHRU;
            } else if (args[i].equals("-I")) {
                String archFile = args[++i];
                this.inStream = new FileInputStream(archFile);
            } else if (args[i].equals("--basedir")) {
                String baseDir = args[++i];
                this.extractBase = new File(baseDir);
            } else if (args[i].equals("--quiet")) {
                this.quiet = true;
            } else if (args[i].equals("-t") || args[i].equals("--list")) {
                this.listMode = true;
            } else if (args[i].equals("-v") || args[i].equals("--verbose")) {
                this.verbose = true;
            } else {
                System.err.println("WARNING: ignoring option '" + args[i] + "'");
            }
        }
    }

    /** */
    private void processCPIO() throws IOException {
        if (this.action == ACT_COPYIN) {
            this.processCopyIn();
        } else if (this.action == ACT_COPYOUT) {
            System.err.println("copyout is unimplemented");
        } else if (this.action == ACT_PASSTHRU) {
            System.err.println("passthru is unimplemented");
        } else {
            System.err.println("no action specified");
            this.usage();
        }
    }

    /** */
    private void processCopyIn() throws IOException {
        CPIOInputStream cin = new CPIOInputStream(this.inStream);

        while (true) {
            CPIOEntry entry = cin.getNextEntry();

            if (entry.getHeader().filename.equals("TRAILER!!!"))
                break;

            // List it
            if (this.listMode || this.verbose) {
                if (this.listMode && this.verbose) {
                    // 'ls -l' style...
                    System.err.println(entry.getHeader().filename + "  " + entry.getHeader().filesize + " bytes.");
                } else {
                    System.err.println(entry.getHeader().filename);
                }
            }

            // Extract it
            if (!this.listMode) {
                File destF = new File(this.extractBase, entry.getHeader().filename);

                FileOutputStream out = new FileOutputStream(destF);

                cin.copyEntryContents(out);

                out.close();
            }
        }
    }

    /** */
    private void usage() {
    }
}
