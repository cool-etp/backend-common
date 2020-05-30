package org.cooletp.common.helper;


import org.cooletp.common.exception.EtpFileException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZipHelper {

    /**
     * Распакуем файл архива
     *
     * @param sourceFile Исходный файл архива, должен существовать физически. Это проверяется
     * @param destinationDir Папка назначения распаковки. Проверяется наличие. Если нет - то создается. Если не пустая - то эксепшн.
     *                       Если файл, то тоже эксепшн
     * @throws EtpFileException Возможные исключения при обработке
     * @return List<String> Возвращаем список распакованных файлов, если такие были
     */
    public static List<String> unZipFile(String sourceFile, String destinationDir) throws EtpFileException {
        List<String> result = new ArrayList<>();
        byte[] buffer = new byte[1024];

        try {
            checkSourceFileExists(sourceFile);
            File destinationDirFile = getDestinationDirFile(destinationDir);

            ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceFile));
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                File newFile = newFileForUnzip(destinationDirFile, zipEntry);
                if(!newFile.isHidden()) {
                    if (zipEntry.isDirectory()) {
                        // На случай вложенных в архив директорий
                        Files.createDirectories(newFile.toPath());
                    } else {
                        // Пропускаем всякие системные скрытые файлы
                        FileOutputStream fos = new FileOutputStream(newFile);

                        int len;

                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }

                        fos.close();
                    }
                }

                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            // Возвращаем только файлы в целевой директории в виде списка строк
            // Архив может содержать вложенную папку - поэтому делаем рекурсивно
            getFilesListInPathRecursively(destinationDirFile.toPath(), result);

        } catch (IOException ex) {
            throw new EtpFileException(ex);
        }

        return result;
    }

    private static void getFilesListInPathRecursively(Path path, List<String> result) throws IOException {
        if(result == null) {
            result = new ArrayList<>();
        }

        List<Path> currentList = Files.list(path).collect(Collectors.toList());

        for(Path p: currentList) {
            if(Files.isDirectory(p)) {
                getFilesListInPathRecursively(p, result);
            } else {
                result.add(p.toString());
            }
        }
    }

    private static File newFileForUnzip(File destinationDirFile, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDirFile, zipEntry.getName());

        String destDirPath = destinationDirFile.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    private static void checkSourceFileExists(String sourceFile) throws EtpFileException {
        if(Files.notExists(Paths.get(sourceFile))) {
            throw new EtpFileException("Файла архива ".concat(sourceFile).concat(" не существует!"));
        }
    }

    private static File getDestinationDirFile(String destinationDir) throws EtpFileException {
        Path destDirPath = Paths.get(destinationDir);

        try {
            if (Files.notExists(destDirPath)) {
                // Если папки не существует - пытаемся ее создать
                Files.createDirectories(destDirPath);
            } else if (Files.isDirectory(destDirPath) && Files.list(destDirPath).count() > 0) {
                // Если папка распаковки существует и она не пустая - так нельзя
                throw new EtpFileException("Папка назначения распаковки не пуста!");
            } else if (Files.exists(destDirPath) && !Files.isDirectory(destDirPath)) {
                // Нельзя распаковать есть конечный путь это файл
                throw new EtpFileException("Путь назначения распаковки является файлом!");
            }
        } catch (IOException ex) {
            throw new EtpFileException(ex);
        }

        return destDirPath.toFile();
    }
}
