package org.cooletp.common.helper;

import org.cooletp.common.exception.EtpFileException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UnZipHelperTest {
    private static final String EXAMPLE_DIR_FOLDER = "examples";
    private static final String EXAMPLE_ZIP_FILE = "unZipTests.txt.zip";

    private static Path pathToExampleFilesDir;
    private static Path pathToTmpDir;

    @BeforeAll
    static void beforeAll() {
        String pathToResourceDir = UnZipHelperTest.class.getResource("/").getPath();
        pathToExampleFilesDir = Paths.get(pathToResourceDir, EXAMPLE_DIR_FOLDER);
    }

    @BeforeEach
    void setUp() throws EtpFileException {
        // Перед запуском каждого теста очищаем временную папку
        pathToTmpDir = IOHelper.createTmpDir("unZipTests");
    }

    @Test
    public void givenSampleZipFile_whenTryToUnzipExisted_thenGetListOfFiles() throws IOException {
        String sourceFile = Paths.get(pathToExampleFilesDir.toString(), EXAMPLE_ZIP_FILE).toString();

        List<String> unzippedList = UnZipHelper.unZipFile(sourceFile, pathToTmpDir.toString());

        assertThat(unzippedList.size()).isEqualTo(3);
    }
}
