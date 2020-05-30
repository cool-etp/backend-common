package org.cooletp.common.ftp;

import org.cooletp.common.exception.EtpFtpException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EtpFtpIntegrationTest {
    private FakeFtpServer fakeFtpServer;

    private EtpFtpClient ftpClient;
    private static FtpProperties props;

    @BeforeAll
    static void beforeAll() {
        props = new FtpProperties();
        props.setServer("localhost");
        props.setPort(2121);
        props.setUsername("user");
        props.setPassword("password");
    }

    @BeforeEach
    void setUp() throws EtpFtpException {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(props.getPort());

        fakeFtpServer.addUserAccount(new UserAccount(props.getUsername(), props.getPassword(), "/"));

        FileSystem fileSystem = new UnixFakeFileSystem();

        fileSystem.add(new DirectoryEntry("/out/nsi/nsiOkved2"));
        fileSystem.add(new DirectoryEntry("/out/nsi/nsiOkato"));
        fileSystem.add(new DirectoryEntry("/out/nsi/nsiOkato/daily"));
        fileSystem.add(new FileEntry("/out/nsi/nsiOkato/nsiOkato_all_20200524_031828_001.xml.zip", "abcdef 1234567890"));
        fileSystem.add(new FileEntry("/out/nsi/nsiOkato/nsiOkato_all_20200525_987654_003.xml.zip", "abcdef 1234567890"));
        fileSystem.add(new FileEntry("/out/nsi/nsiOkato/daily/nsiOkato_inc_20200524_031824_001.xml.zip", "abcdef 1234567890"));
        fileSystem.add(new FileEntry("/out/nsi/nsiOkato/daily/nsiOkato_inc_20200524_031821_001.xml.zip", "abcdef 1234567890"));
        fileSystem.add(new FileEntry("/out/nsi/nsiOkato/daily/nsiOkato_inc_20200525_031827_002.xml.zip", "abcdef 1234567890"));
        fileSystem.add(new FileEntry("/out/nsi/nsiOkato/daily/nsiOkato_inc_20200525_031812_001.xml.zip", "abcdef 1234567890"));
        fakeFtpServer.setFileSystem(fileSystem);

        fakeFtpServer.start();

        ftpClient = new EtpFtpClient(props);
        ftpClient.open();
    }

    @AfterEach
    void tearDown() throws EtpFtpException {
        ftpClient.close();
        fakeFtpServer.stop();
    }

    @Test
    public void givenRemoteFile__whenListingRemoteFiles__thenItIsContainedInList() throws EtpFtpException {
        List<String> files = ftpClient.listFiles("/out/nsi/nsiOkato");
        assertThat(files).contains("nsiOkato_all_20200524_031828_001.xml.zip");
        assertThat(files).contains("nsiOkato_all_20200525_987654_003.xml.zip");

        files = ftpClient.listFiles("/out/nsi/nsiOkato/daily");
        assertThat(files.size()).isEqualTo(4);
        assertThat(files).contains("nsiOkato_inc_20200524_031821_001.xml.zip");
    }

    @Test
    public void givenRemoteFile_getLatestFileInPath_fileExists() throws EtpFtpException {
        String latestFile = ftpClient.getLatestFile("/out/nsi/nsiOkato");

        assertThat(latestFile).isEqualTo("nsiOkato_all_20200525_987654_003.xml.zip");
    }

    @Test
    public void givenRemoteFile_getLatestFileInPath_pathEmpty() throws EtpFtpException {
        try {
            String latestFile = ftpClient.getLatestFile("/out/nsi/nsiOkved2");
        } catch (EtpFtpException ex) {
            assertThat(ex).hasMessageContaining("Directory /out/nsi/nsiOkved2 is empty!");
        }
    }

    @Test
    public void givenRemoteFile_getFilesForDate_filesExists() throws EtpFtpException {
        List<String> files = ftpClient.getFilesForDate("/out/nsi/nsiOkato/daily", LocalDate.of(2020, 5, 25));

        assertThat(files.size()).isEqualTo(2);
        assertThat(files.get(0)).isEqualTo("nsiOkato_inc_20200525_031812_001.xml.zip");
        assertThat(files.get(1)).isEqualTo("nsiOkato_inc_20200525_031827_002.xml.zip");
    }

    @Test
    public void givenRemoteFile_getFilesForDate_emptyListOfFiles() throws EtpFtpException {
        List<String> files = ftpClient.getFilesForDate("/out/nsi/nsiOkato/daily", LocalDate.of(2020, 5, 21));

        assertThat(files.size()).isEqualTo(0);
    }

    @Test
    public void givenRemoteFile__whenDownloading__thenItIsOnTheLocalFilesystem() throws EtpFtpException {
        String srcFileName = "/out/nsi/nsiOkato/daily/nsiOkato_inc_20200525_031812_001.xml.zip";
        String destFileName = "/tmp/nsiOkatoDownloaded.xml.zip";

        ftpClient.downloadFile(srcFileName, destFileName);
        assertThat(new File(destFileName)).exists();

        new File(destFileName).delete();//cleanup
    }

    @Test
    public void givenRemoteFile__whenDownloading__thenItIsNotOnTheFtpServer() throws EtpFtpException {
        String srcFileName = "/out/nsi/nsiOkato/daily/nsiOkato_inc_20200525_031812_012.xml.zip";
        String destFileName = "/tmp/nsiOkatoDownloaded.xml.zip";
        try {
            ftpClient.downloadFile(srcFileName, destFileName);
        } catch (EtpFtpException ex) {
            assertThat(ex).hasMessageContaining("File " + srcFileName + " not found on server");
        }
    }
}
