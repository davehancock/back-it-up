package com.otf.backup.client.dropbox;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.users.FullAccount;
import com.otf.backup.client.BackupClient;
import com.otf.backup.file.FileSystemHelper;
import com.otf.backup.file.archive.ArchiveService;
import com.otf.backup.file.compress.CompressionService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;


/**
 * TODO Abstract the operations behind more fine grained interfaces for behaviour such as unzipping, compressing, taring etc.
 * <p>
 * TODO Refactor the below to be small atomic units that return paths / files etc not assume locations from outside
 *
 * @author David Hancock
 */
public class DropboxBackupClient implements BackupClient {

    private static final Logger LOG = LoggerFactory.getLogger(DropboxBackupClient.class);

    private FileSystemHelper fileSystemHelper = new FileSystemHelper();

    private CompressionService compressionService;

    private ArchiveService archiveService;

    private DbxClientV2 client;


    public DropboxBackupClient(CompressionService compressionService, ArchiveService archiveService, DbxClientV2 client) {
        this.compressionService = compressionService;
        this.archiveService = archiveService;
        this.client = client;
    }

    @Override
    public void backupFile(String archiveDirectory, String backupFilename, String remotePath) {

        logUserAccount();

        archiveFiles(archiveDirectory, backupFilename);

        uploadNewBackup(backupFilename, remotePath);

        archivePrimaryBackup(remotePath);

        cleanupBackupUpload(remotePath);
    }

    @Override
    public void restoreFile(String outputDirectory, String remotePath) {

        logUserAccount();

        // Create folder of which restore to
        fileSystemHelper.createOutputDirectory(outputDirectory);

        // Unzip and unarchive to the restore location
        File downloadedBackup = downloadBackupFile(outputDirectory, remotePath);
        File decompressedFile = compressionService.decompressFile(downloadedBackup, outputDirectory);
        archiveService.unarchiveFile(decompressedFile, outputDirectory);

        // Cleanup intermediary files
        fileSystemHelper.deleteFile(downloadedBackup);
        fileSystemHelper.deleteFile(decompressedFile);
    }

    @Override
    public void retrieveAgeOfFile(String outputDirectory, String remotePath) {

        logUserAccount();

        fileSystemHelper.createOutputDirectory(outputDirectory);

        int age = determineAgeOfFile(remotePath);

        String ageFilePath = outputDirectory + "/age";
        fileSystemHelper.writeFile(ageFilePath, String.valueOf(age));
    }

    @Override
    public void deleteFile(String remotePath) {

        logUserAccount();

        deleteRemoteFile(remotePath);
    }


    // FIXME This assumes a lot...
    private void archiveFiles(String archiveDirectory, String backupFilename) {

        // FIXME Hacky, assumes this is a .gz
        String archiveFilePath = backupFilename.substring(0, backupFilename.length() - 3);
        File archivedFile = new File(archiveFilePath);
        archiveService.archiveFile(archiveDirectory, archivedFile);

        File compressedFile = new File(backupFilename);
        compressionService.compressFile(archivedFile, compressedFile);

        // Cleanup intermediary files
        fileSystemHelper.deleteFile(archivedFile);
    }

    private File downloadBackupFile(String outputDirectory, String remotePath) {

        File file = new File(outputDirectory + remotePath);

        try {
            InputStream inputStream = client.files().download(remotePath).getInputStream();

            LOG.info("Found remote file at '{}', downloading...", remotePath);
            FileUtils.copyInputStreamToFile(inputStream, file);
            LOG.info("File successfully downloaded to: '{}'", outputDirectory);

        } catch (DbxException | IOException e) {
            throw new RuntimeException("Error during file download", e);
        }

        return file;
    }

    private void uploadNewBackup(String backupFilename, String remotePath) {

        try (InputStream in = new FileInputStream(backupFilename)) {

            // Create this file as tmp while we ensure the other steps succeed.
            remotePath += ".tmp";

            LOG.info("Uploading file at '{}' to '{}'...", backupFilename, remotePath);
            client.files().uploadBuilder(remotePath).uploadAndFinish(in);
            LOG.info("File successfully uploaded to: '{}'", remotePath);

            // Cleanup intermediary files
            fileSystemHelper.deleteFile(new File(backupFilename));

        } catch (DbxException | IOException e) {
            throw new RuntimeException("Error during file upload", e);
        }
    }

    private void archivePrimaryBackup(String primaryBackupPath) {

        try {
            boolean primaryBackupExists = checkDropboxFileExists(primaryBackupPath);

            String timestamp = Instant.now().toString();
            String archivePath = primaryBackupPath + "-" + timestamp;

            if (primaryBackupExists) {
                LOG.info("Archiving primary backup: '{}' to '{}'...", primaryBackupPath);
                client.files().move(primaryBackupPath, archivePath);
                LOG.info("Primary backup archived");
            } else {
                LOG.info("No primary backup was found at: '{}'", primaryBackupPath);
            }

        } catch (DbxException e) {
            throw new RuntimeException("Error during the archiving of the primary backup file", e);
        }

    }

    private void cleanupBackupUpload(String remotePath) {

        try {
            String tmpRemotePath = remotePath + ".tmp";

            LOG.info("Copying tmp backup from '{}' to '{}'...", tmpRemotePath, remotePath);
            client.files().move(tmpRemotePath, remotePath);
            LOG.info("Tmp backup was copied to primary backup successfully");

        } catch (DbxException e) {
            throw new RuntimeException("Error during file upload cleanup", e);
        }
    }

    private int determineAgeOfFile(String remotePath) {

        try {
            LOG.info("Determining age of file: '{}'", remotePath);
            DbxDownloader<FileMetadata> metadata = client.files().download(remotePath);
            FileMetadata fileMetadata = metadata.getResult();

            LOG.info("Date of file was '{}'", fileMetadata.getServerModified().toString());

            // TODO Hacky Date logic
            Date modifiedDate = fileMetadata.getServerModified();
            Instant instant = Instant.ofEpochMilli(modifiedDate.getTime());
            LocalDate modifiedLocalDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
            long age = ChronoUnit.DAYS.between(modifiedLocalDate, LocalDate.now());

            LOG.info("Age of file, in days, calculated to be '{}'", age);

            return Math.toIntExact(age);

        } catch (DbxException e) {
            throw new RuntimeException("Error during determining of age of file", e);
        }

    }

    private void deleteRemoteFile(String remotePath) {

        try {

            LOG.info("Deleting remote file at '{}'", remotePath);
            client.files().delete(remotePath);
            LOG.info("File successfully deleted.");

        } catch (DbxException e) {
            throw new RuntimeException("Error during file deletion", e);
        }

    }

    private void logUserAccount() {

        try {
            FullAccount account = client.users().getCurrentAccount();
            LOG.info("Dropbox User account display name is: '{}'", account.getName().getDisplayName());
        } catch (DbxException e) {
            throw new RuntimeException("Error reading User Account information", e);
        }
    }

    /**
     * A very blunt way to check if a dropbox file exists, the alternative - Search API - has latency and does not produce
     * up to date results, i.e its inconsistent.
     */
    private boolean checkDropboxFileExists(String path) {

        boolean fileExists = true;

        try {
            client.files().getMetadata(path);
        } catch (DbxException e) {
            fileExists = false;
        }

        return fileExists;
    }

}
