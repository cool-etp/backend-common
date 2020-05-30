package org.cooletp.common.helper;

import org.cooletp.common.exception.EtpFileException;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class IOHelperTest {

    @Test
    public void givenNothing_whenRequestTmpDirPath_thenCheckItIsExists() throws EtpFileException {
        // Сначала создадим папку
        IOHelper.createTmpDir("testDir");
        // Проверим что создалась
        Path tmpDirPath = IOHelper.getTmpDir("testDir");

        assertThat(Files.exists(tmpDirPath)).isTrue();
        assertThat(Files.isDirectory(tmpDirPath)).isTrue();
    }
}
