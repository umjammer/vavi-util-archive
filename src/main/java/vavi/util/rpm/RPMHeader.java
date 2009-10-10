
package vavi.util.rpm;

import java.io.PrintStream;


public class RPMHeader extends Object {
    public byte[] magic = new byte[3];

    public byte version = 0;

    public byte[] reserved = new byte[4];

    public int numEntries;

    public int headerSize;

    public void dump(PrintStream out, String prefix) {
        out.println(prefix + "Magic:    " + RPMUtil.hexString(this.magic));
        out.println(prefix + "Version:  " + this.version);
        out.println(prefix + "Reserved: " + RPMUtil.hexString(this.reserved));
        out.println(prefix + "Entries:  " + this.numEntries);
        out.println(prefix + "Size:     " + this.headerSize);
    }

}
