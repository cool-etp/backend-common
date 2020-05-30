package org.cooletp.common.helper;

import org.cooletp.common.exception.EtpFileException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class IOHelper {
    private final static String rootTmpDir = IOHelper.class.getPackageName();

    public static Path createTmpDir(String name) throws EtpFileException {
        try {
            Path tmpDirPath = getTmpDir(name);
            // Удалим сначала временную папку со всем содержимым
            deleteDirectoryRecursively(tmpDirPath);
            Files.createDirectory(tmpDirPath);

            return tmpDirPath;
        } catch(EtpFileException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new EtpFileException(ex);
        }
    }

    public static Path getTmpDir(String name) throws EtpFileException {
        String rootTmpPath = createRootTmpDir().toString();
        return Paths.get(rootTmpPath, name);
    }

    private static String getRootTmpPath() {
        return System.getProperty("java.io.tmpdir");
    }

    private static void deleteDirectoryRecursively(Path dirPath) throws EtpFileException {
        try {
            // если указанной папки нет - ничего не делаем
            if(!Files.exists(dirPath)) {
                return;
            }

            if (Files.isDirectory(dirPath) && Files.list(dirPath).count() != 0) {
                // Сначала удалим все обычные файлы в директории
                Files.list(dirPath).filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);

                // Теперь рекурсивно удалим все папки
                for (Path path : Files.list(dirPath).filter(Files::isDirectory).collect(Collectors.toList())) {
                    deleteDirectoryRecursively(path);
                }
            }
            // И удалим саму (Теперь пустую) папку
            Files.delete(dirPath);
        } catch (IOException ex) {
            throw new EtpFileException(ex);
        }
    }

    /**
     * Проверим существует ли корневая временная директория
     * Если нет - создаем
     */
    private static Path createRootTmpDir() throws EtpFileException {
        Path fullTmpRootPath = Paths.get(getRootTmpPath(), rootTmpDir);

        // На случай если есть файл с таким же именем как директория - на всякий не будем удалять его
        if(Files.exists(fullTmpRootPath) && !Files.isDirectory(fullTmpRootPath)) {
            throw new EtpFileException("Tmp Root Dir cannot be created. File with same name exists. (" + fullTmpRootPath.toString() + ")");
        }

        try {
            if(Files.notExists(fullTmpRootPath)) {
                Files.createDirectory(fullTmpRootPath);
            }

            return fullTmpRootPath;
        } catch (IOException ex) {
            throw new EtpFileException(ex);
        }
    }
}
