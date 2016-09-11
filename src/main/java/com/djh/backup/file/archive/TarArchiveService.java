package com.djh.backup.file.archive;

import com.djh.backup.file.compress.GzipCompressionService;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;

/**
 * @author David Hancock
 */
public class TarArchiveService implements ArchiveService {

    private static final Logger LOG = LoggerFactory.getLogger(GzipCompressionService.class);

    @Override
    public void archiveFile(String directoryToArchive, File outputArchiveFile) {

        LOG.info("Archiving directory: '{}' to archive file: '{}'", directoryToArchive, outputArchiveFile.getAbsolutePath());

        try (TarArchiveOutputStream archiveOutputStream = new TarArchiveOutputStream(new FileOutputStream(outputArchiveFile))) {
            archiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

            LOG.info("Attempting to read files in archive directory '{}'.", directoryToArchive);

            // Read in input directory and get a list of all files
            File archiveDirectory = new File(directoryToArchive);
            Collection<File> fileIterator = FileUtils.listFilesAndDirs(archiveDirectory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);


            // Iterate over each file listed from the input directory and create and write a Tar entry for each
            for (File file : fileIterator) {

                LOG.info("Attempting to add file '{}' to the archive.", file.getCanonicalPath());

                String entryRelativePath = file.getCanonicalPath().replaceAll(directoryToArchive, ".");

                TarArchiveEntry entry = new TarArchiveEntry(file, entryRelativePath);
                archiveOutputStream.putArchiveEntry(entry);
                entry.setSize(file.length());

                if (file.isFile()) {
                    IOUtils.copy(new FileInputStream(file), archiveOutputStream);
                }

                archiveOutputStream.closeArchiveEntry();
            }

        } catch (IOException e) {
            throw new RuntimeException("Archiving failed", e);
        }

        LOG.info("Successfully Archived directory to file: '{}'", outputArchiveFile.getAbsolutePath());

    }

    @Override
    public void unarchiveFile(File inputArchiveFile, String outputDirectory) {

        LOG.info("Unarchiving file: '{}' using Tar to output directory: '{}'", inputArchiveFile.getAbsolutePath(), outputDirectory);

        try (ArchiveInputStream archiveInputStream = new ArchiveStreamFactory().createArchiveInputStream(
                ArchiveStreamFactory.TAR, new FileInputStream(inputArchiveFile))) {

            ArchiveEntry entry;
            while ((entry = archiveInputStream.getNextEntry()) != null) {

                final File outputFile = new File(outputDirectory, entry.getName());

                if (entry.isDirectory()) {
                    LOG.info("Attempting to write output directory '{}'.", outputFile.getAbsolutePath());
                    if (!outputFile.exists()) {
                        LOG.info("Attempting to create output directory '{}'.", outputFile.getAbsolutePath());
                        if (!outputFile.mkdirs()) {
                            throw new RuntimeException("Failed to create directory at: " + outputFile.getAbsolutePath());
                        }
                    }
                } else {
                    LOG.info("Creating output file '{}'.", outputFile.getAbsolutePath());
                    final OutputStream outputFileStream = new FileOutputStream(outputFile);
                    IOUtils.copy(archiveInputStream, outputFileStream);
                    outputFileStream.close();
                }
            }

        } catch (IOException | ArchiveException e) {
            throw new RuntimeException("Unarchiving failed", e);
        }

        LOG.info("Successfully Unarchived file to directory: '{}'", outputDirectory);
    }

}
