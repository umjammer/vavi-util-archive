package vavi.util.codec.rpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import vavi.util.Debug;
import vavi.util.codec.cpio.CPIOEntry;


public class rpmdump {

    @SuppressWarnings("unused")
    private boolean extractOnly = false;

    @SuppressWarnings("unused")
    private File baseDirF = null;

    public static void main(String[] args) throws Exception {
        rpmdump app = new rpmdump();

        if (args.length < 1) {
            rpmdump.usage();
            System.exit(1);
        }

        FileInputStream fin = null;
        RPMInputStream rin = null;

        app.baseDirF = new File(System.getProperty("user.dir", "."));

        args = rpmdump.processArguments(args);

        if (args.length < 1) {
            System.err.println("no rpm file specified");
            rpmdump.usage();
            System.exit(1);
        }

        fin = new FileInputStream(args[0]);

        rin = new RPMInputStream(fin, "name", "version", "release", "arch");

        RPMLead lead = rin.getRPMLead();

        Debug.println("RPM Lead:");
        lead.dump(System.err, "   ");

        RPMHeader sigHeader = rin.getSignatureHeader();

        Debug.println("RPM Signature Header:");
        sigHeader.dump(System.err, "   ");

        for (int i = 0; i < sigHeader.numEntries; ++i) {
            RPMIndexEntry entry = rin.getSignatureEntry(i);
            if (entry != null) {
                Debug.println("   RPM Index Entry #" + i + ":");
                entry.dump(System.err, "   ");
            } else {
                Debug.println("Entry #" + i + " is null!");
            }
        }

        RPMHeader headHeader = rin.getHeaderHeader();

        Debug.println("RPM Header Header:");
        headHeader.dump(System.err, "   ");

        int idx;
        for (idx = 0; idx < headHeader.numEntries; ++idx) {
            RPMIndexEntry entry = rin.getHeaderEntry(idx);
            if (entry != null) {
                Debug.println("   RPM Index Entry #" + idx + ":");
                entry.dump(System.err, "   ");
            } else {
                Debug.println("Entry #" + idx + " is null!");
            }
        }

        Debug.println("RPM Archive Files:");
        while (true) {
            try {
                CPIOEntry entry = rin.getNextFileEntry();

                if (entry.getHeader().filename.equals("TRAILER!!!"))
                    break;

                System.out.println("   " + entry.getHeader().filename + "  " + entry.getHeader().filesize + " bytes.");
            } catch (IOException ex) {
                Debug.printStackTrace(ex);
                break;
            }
        }
    }

    private static void usage() {
        System.out.println("usage: rpmdump [options...] rpmfile");
    }

    private static String[] processArguments(String[] args) {
        int idx = 0;

        for (; idx < args.length; ++idx) {
            if (!args[idx].startsWith("-") || args[idx].equals("--")) {
                break;
            }
        }

        String[] result = new String[args.length - idx];
        System.arraycopy(args, idx, result, 0, (args.length - idx));

        return result;
    }

}
