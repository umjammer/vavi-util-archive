
package vavi.util.cpio;

import java.io.IOException;
import java.io.InputStream;


public class CPIOEntry extends Object {
    private CPIOHeader header;

    public CPIOEntry(InputStream in /* , boolean readContents */) throws IOException {
        this.header = new CPIOHeader(in);
    }

    public CPIOHeader getHeader() {
        return this.header;
    }
}
