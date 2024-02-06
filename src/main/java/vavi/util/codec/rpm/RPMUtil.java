
package vavi.util.codec.rpm;

public class RPMUtil {
    public static String hexString(byte[] buf) {
        return RPMUtil.hexString(buf, 0, buf.length);
    }

    public static String hexString(byte[] buf, int off, int len) {
        return RPMUtil.hexString(buf, off, len, 2, " ");
    }

    public static String hexString(byte[] buf, int off, int len, int sepFreq, String sep) {
        char[] hexChars = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };

        StringBuilder result = new StringBuilder();

        int sepCnt = 1;
        for (int i = off; i < (off + len); ++i) {
            result.append(hexChars[(buf[i] >> 4) & 15]);
            result.append(hexChars[buf[i] & 15]);
            if (sepCnt++ == sepFreq) {
                sepCnt = 1;
                result.append(sep);
            }
        }

        return result.toString();
    }

    public static String hexDumpString(String prefix, byte[] buf, int off, int len) {
        int incr = 16;
        int count = 0;
        int offset = off;

        byte[] offBuf = new byte[4];
        StringBuilder result = new StringBuilder();

        while (count < len) {
            int num = ((count + incr) > len) ? (len - count) : incr;

            result.append(prefix);

            // EKE! I don't like this code!!! Use com.ice.text.HexFormat?
            offBuf[0] = (byte) ((count >> 24) & 255);
            offBuf[1] = (byte) ((count >> 16) & 255);
            offBuf[2] = (byte) ((count >> 8) & 255);
            offBuf[3] = (byte) ((count >> 0) & 255);

            result.append(RPMUtil.hexString(offBuf, 0, 4, 4, ""));
            result.append("  ");

            result.append(RPMUtil.hexString(buf, offset, num, 2, " "));

            result.append("\n");

            count += num;
            offset += num;
        }

        return result.toString();
    }

}
