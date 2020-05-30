package org.cooletp.common.ftp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FtpProperties implements IFtpProperties {
    private String server = "localhost";
    private int port = 21;
    private String username = "user";
    private String password = "pass";
}
