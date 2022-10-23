
package vavi.util.codec.rpm;

import java.io.IOException;


public class InvalidRPMFileException extends IOException {

    public InvalidRPMFileException() {
        super();
    }

    public InvalidRPMFileException(String msg) {
        super(msg);
    }
}
