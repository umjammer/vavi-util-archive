package vavi.util.rpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import vavi.util.cpio.CPIOEntry;


public class rpmdump {

    @SuppressWarnings("unused")
    private boolean extractOnly = false;

    @SuppressWarnings("unused")
    private File baseDirF = null;

    public static void main(String[] args) {
        rpmdump app = new rpmdump();

        if (args.length < 1) {
            app.usage();
            System.exit(1);
        }

        FileInputStream fin = null;
        RPMInputStream rin = null;

        app.baseDirF = new File(System.getProperty("user.dir", "."));

        args = app.processArguments(args);

        if (args.length < 1) {
            System.err.println("no rpm file specified");
            app.usage();
            System.exit(1);
        }

        try {
            fin = new FileInputStream(args[0]);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        try {
            rin = new RPMInputStream(fin, "name", "version", "release", "arch");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        RPMLead lead = rin.getRPMLead();

        System.err.println("RPM Lead:");
        lead.dump(System.err, "   ");

        RPMHeader sigHeader = rin.getSignatureHeader();

        System.err.println("RPM Signature Header:");
        sigHeader.dump(System.err, "   ");

        for (int i = 0; i < sigHeader.numEntries; ++i) {
            RPMIndexEntry entry = rin.getSignatureEntry(i);
            if (entry != null) {
                System.err.println("   RPM Index Entry #" + i + ":");
                entry.dump(System.err, "   ");
            } else {
                System.err.println("Entry #" + i + " is null!");
            }
        }

        RPMHeader headHeader = rin.getHeaderHeader();

        System.err.println("RPM Header Header:");
        headHeader.dump(System.err, "   ");

        int idx;
        for (idx = 0; idx < headHeader.numEntries; ++idx) {
            RPMIndexEntry entry = rin.getHeaderEntry(idx);
            if (entry != null) {
                System.err.println("   RPM Index Entry #" + idx + ":");
                entry.dump(System.err, "   ");
            } else {
                System.err.println("Entry #" + idx + " is null!");
            }
        }

        System.err.println("RPM Archive Files:");
        for (;;) {
            try {
                CPIOEntry entry = rin.getNextFileEntry();

                if (entry.getHeader().filename.equals("TRAILER!!!"))
                    break;

                System.err.println("   " + entry.getHeader().filename + "  " + entry.getHeader().filesize + " bytes.");
            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            }
        }
    }

    private void usage() {
        System.err.println("usage: rpmdump [options...] rpmfile");
    }

    private String[] processArguments(String[] args) {
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
