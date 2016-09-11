package com.djh.backup.client.dropbox;

import com.djh.backup.BackupConfiguration;
import com.djh.backup.client.BackupClient;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * FIXME Make this a runnable integration test in CI
 *
 * @author David Hancock
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BackupConfiguration.class)
@TestPropertySource(properties = {"accessToken = SECRET"})
@Ignore
public class DropboxBackupClientIntegrationTest {

    private static final String REMOTE_PATH = "/testInputDir.tar.gz";

    private static final String ARCHIVE_DIRECTORY = "/Users/Dave/IdeaProjects/back-it-up/src/test/resources/backup/testInputDir";
    private static final String BACKUP_FILENAME = "/Users/Dave/IdeaProjects/back-it-up/src/test/resources/backup/testInputDir.tar.gz";

    private static final String RESTORE_DIRECTORY = "/Users/Dave/IdeaProjects/back-it-up/src/test/resources/restore";

    private static final String OUTPUT_AGE_DIRECTORY = "/Users/Dave/IdeaProjects/back-it-up/src/test/resources/age";


    @Inject
    private BackupClient backupClient;


    @Test
    public void backupFile() {

        backupClient.backupFile(ARCHIVE_DIRECTORY, BACKUP_FILENAME, REMOTE_PATH);
    }

    @Test
    public void restoreFile() {

        backupClient.restoreFile(RESTORE_DIRECTORY, REMOTE_PATH);
    }

    @Test
    public void getAgeOfFile() {

        backupClient.retrieveAgeOfFile(OUTPUT_AGE_DIRECTORY, REMOTE_PATH);
    }

    @Test
    public void deleteFile() {

        backupClient.deleteFile(REMOTE_PATH);
    }

}
