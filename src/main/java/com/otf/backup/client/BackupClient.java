package com.otf.backup.client;

/**
 * TODO Storage client?
 *
 * @author David Hancock
 */
public interface BackupClient {


    void backupFile(String archiveDirectory, String backupFilename, String remotePath);

    void restoreFile(String restoreFilename, String remotePath);

    void retrieveAgeOfFile(String outputDirectory, String remotePath);

    void deleteFile(String remotePath);


}
