package org.cooletp.common.exception;

import java.io.IOException;

public class EtpFtpException extends IOException {
    public EtpFtpException() {
    }

    public EtpFtpException(String message) {
        super(message);
    }

    public EtpFtpException(String message, Throwable cause) {
        super(message, cause);
    }

    public EtpFtpException(Throwable cause) {
        super(cause);
    }
}
