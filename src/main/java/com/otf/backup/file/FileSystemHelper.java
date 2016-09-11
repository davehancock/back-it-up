package com.otf.backup.file;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author David Hancock
 */
public class FileSystemHelper {

    private static final Logger LOG = LoggerFactory.getLogger(FileSystemHelper.class);

    public void createOutputDirectory(String outputDirectory) {

        LOG.info("Creating Directory at: '{}", outputDirectory);

        try {
            FileUtils.forceMkdir(new File(outputDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Error during output directory creation", e);
        }
    }

    public void deleteFile(File file) {

        LOG.info("Attempting to delete file at: '{}", file.getAbsolutePath());

        boolean isFileDeleted = FileUtils.deleteQuietly(file);

        if (isFileDeleted) {
            LOG.info("File successfully deleted!");
        } else {
            LOG.info("Error could not delete file at: '{}", file.getAbsolutePath());
        }
    }

    public void writeFile(String outputFile, String content) {

        LOG.info("Creating new file at: '{}', with content: '{}'", outputFile, content);

        try {
            FileUtils.write(new File(outputFile), content, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Error during file creation", e);
        }
    }

}
