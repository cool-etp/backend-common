package org.cooletp.common.ftp;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.cooletp.common.exception.EtpFtpException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class EtpFtpClient {
    private FTPClient ftpClient;
    private final IFtpProperties props;

    public EtpFtpClient(IFtpProperties props) {
        this.props = props;
    }

    public void open() throws EtpFtpException {
        try {
            ftpClient = new FTPClient();

            ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

            ftpClient.connect(props.getServer(), props.getPort());

            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new IOException("Ошибка подключения к FTP серверу (код " + reply + ")");
            }

            ftpClient.enterLocalPassiveMode();

            if (!ftpClient.login(props.getUsername(), props.getPassword())) {
                reply = ftpClient.getReplyCode();

                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                    throw new IOException("Ошибка подключения к FTP серверу. Ошибка авторизации. (код " + reply + ")");
                }
            }
        } catch (IOException ex) {
            throw new EtpFtpException(ex.getMessage());
        }
    }

    public void close() throws EtpFtpException {
        try {
            ftpClient.disconnect();
        } catch (IOException ex) {
            throw new EtpFtpException(ex.getMessage());
        }

    }

    /**
     * Возвращаем список файлов на ФТП по указанному пути
     * используем List потому как нужен доступ по индексу
     *
     * @param path путь на сервере относительно домашней директории
     * @return список найденных элекментов
     * @throws EtpFtpException если что то пошло не так
     */
    public List<String> listFiles(String path) throws EtpFtpException {
        try {
            FTPFile[] files = ftpClient.listFiles(path);

            return Arrays.stream(files)
                    .map(FTPFile::getName)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new EtpFtpException(ex.getMessage());
        }
    }

    /**
     * Получаем "последний" файл по указанному пути ФТП
     * Сортировку проводим в соответствие с именем файла, так как оно включает дату создания
     *
     * @param path in users home directory
     * @return String name of file in path
     * @throws EtpFtpException if directory in path empty
     */
    public String getLatestFile(String path) throws EtpFtpException {
        try {
            // Список файлов БЕЗ директорий
            FTPFile[] files = ftpClient.listFiles(path, FTPFile::isFile);

            if (files.length == 0) {
                throw new IOException("Directory " + path + " is empty!");
            }

            // Сортируем по имени в обратном порядке
            Arrays.sort(files, (f1, f2) -> f2.getName().compareTo(f1.getName()));

            return files[0].getName();
        } catch (IOException ex) {
            throw new EtpFtpException(ex.getMessage());
        }
    }

    /**
     * Получаем список файлов у которых в названии указана нужная нам дата
     * Шаблон имени файла такой
     * <nsiName>_<type>_<date>_<id>_<orderNum>.xml.zip
     *
     * @param path in users home directory
     * @param date date for which search a file(s)
     * @return Collection of files (if exists) ordered by orderNum ACC
     * @throws EtpFtpException ftpClient communication can throw an exception
     */
    public List<String> getFilesForDate(String path, LocalDate date) throws EtpFtpException {
        try {
            FTPFile[] files = ftpClient.listFiles(
                    path,
                    ftpFile -> ftpFile.getName().split("_")[2].equals(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            );

            // Сортируем по orderNum
            Arrays.sort(files, (f1, f2) -> {
                String fName1 = f1.getName();
                String fName2 = f2.getName();

                return fName1.split("_")[4].compareTo(fName2.split("_")[4]);
            });

            return Arrays.stream(files)
                    .map(FTPFile::getName)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new EtpFtpException(ex.getMessage());
        }
    }

    public void downloadFile(String srcPath, String destPath) throws EtpFtpException {
        try {
            FileOutputStream out = new FileOutputStream(destPath);
            ftpClient.retrieveFile(srcPath, out);

            int reply = ftpClient.getReplyCode();

            if (reply == FTPReply.FILE_UNAVAILABLE) {
                throw new EtpFtpException("File " + srcPath + " not found on server");
            }
        } catch (IOException ex) {
            throw new EtpFtpException(ex.getMessage());
        }
    }
}
