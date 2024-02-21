package vavi.util.codec.rpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import vavi.util.Debug;
import vavi.util.codec.cpio.CPIOEntry;


public class rpm {

    private boolean extractOnly = false;
    private File baseDirF = null;
    private int verbosity = 0;

    public static void main(String[] args) throws Exception {
        rpm app = new rpm();

        RPMInputStream rin = null;
        FileInputStream fin = null;

        app.baseDirF = new File(System.getProperty("user.dir", "."));

        args = app.processArguments(args);

        if (args.length < 1) {
            System.err.println("no rpm file specified");
            rpm.usage();
            System.exit(1);
        }

        fin = new FileInputStream(args[0]);

        rin = new RPMInputStream(fin, "name", "version", "release", "arch");

        RPMLead lead = rin.getRPMLead();
Debug.println(lead);
        RPMHeader sigHeader = rin.getSignatureHeader();
        Debug.println(sigHeader);
        RPMHeader headHeader = rin.getHeaderHeader();
        Debug.println(headHeader);

        while (true) {
            try {
                CPIOEntry entry = rin.getNextFileEntry();

                if (entry.getHeader().filename.equals("TRAILER!!!"))
                    break;

Debug.println(entry.getHeader().filename + "  " + entry.getHeader().filesize + " bytes.");

                if (app.extractOnly) {
                    if (app.verbosity > 0) {
Debug.println(entry.getHeader().filename + "  " + entry.getHeader().filesize + " bytes");
                    }

                    String fName = entry.getHeader().filename;
                    fName = fName.replace('/', File.separatorChar);
                    File exF = new File(app.baseDirF, fName);
                    FileOutputStream fout = new FileOutputStream(exF);
                    rin.copyEntryContents(fout);
                    fout.close();
                }
            } catch (IOException ex) {
                Debug.printStackTrace(ex);
                break;
            }
        }
    }

    private static void usage() {
        System.err.println("usage: rpm [options...] rpmfile");
        System.err.println("options:");
        System.err.println("   --extract-only - the same as -X");
        System.err.println("   -X             - only extract archive files");
        System.err.println("   --root path    - root directory for operation");
    }

    private String[] processArguments(String[] args) {
        int idx = 0;

        for (; idx < args.length; ++idx) {
            if (!args[idx].startsWith("-") || args[idx].equals("--")) {
                break;
            }

            if (args[idx].equals("-X") || args[idx].equals("--extract-only")) {
                this.extractOnly = true;
            } else if (args[idx].equals("--root")) {
                this.baseDirF = new File(args[++idx]);
            } else if (args[idx].startsWith("-v")) {
                this.verbosity += args[idx].length() - 1;
            }
        }

        String[] result = new String[args.length - idx];
        System.arraycopy(args, idx, result, 0, (args.length - idx));

        return result;
    }
}
