
package vavi.util.codec.rpm;

import java.io.PrintStream;


public class RPMLead {

    public byte[] magic = new byte[4];

    public byte major = 0;

    public byte minor = 0;

    public int type = 0;

    public int archNum = 0;

    public byte[] name = new byte[66];

    public int osNum = 0;

    public int signatureType = 0;

    public byte[] reserved = new byte[16];

    public String getName() {
        int i = 0;
        for (; i < this.name.length; ++i)
            if (this.name[i] == '\0')
                break;

        return new String(this.name, 0, i);
    }

    public void dump(PrintStream out, String prefix) {
        out.println(prefix + "Magic:    " + RPMUtil.hexString(this.magic));
        out.println(prefix + "Major:    " + this.major);
        out.println(prefix + "Minor:    " + this.minor);
        out.println(prefix + "Type:     " + this.type);
        out.println(prefix + "ArchNum:  " + this.archNum);
        out.println(prefix + "Name:     " + this.getName());
        out.println(prefix + "OSNum:    " + this.osNum);
        out.println(prefix + "SigType:  " + this.signatureType);
        out.println(prefix + "Reserved: " + RPMUtil.hexString(this.reserved));
    }
}
