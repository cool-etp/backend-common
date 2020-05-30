package org.cooletp.common.exception;

import java.io.IOException;

public class EtpFileException extends IOException {
    public EtpFileException() {
    }

    public EtpFileException(String message) {
        super(message);
    }

    public EtpFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public EtpFileException(Throwable cause) {
        super(cause);
    }
}
