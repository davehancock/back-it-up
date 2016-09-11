package com.otf.backup.file.archive;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/**
 * FIXME Make this a runnable integration test in CI
 *
 * @author David Hancock
 */
@Ignore
public class TarArchiveServiceIntegrationTest {

    private static final String TEST_FILE_TO_UNARCHIVE = "/Users/Dave/IdeaProjects/back-it-up/src/test/resources/unarchive/testInputDir.tar";
    private static final String TEST_UNARCHIVE_OUTPUT_DIRECTORY = "/Users/Dave/IdeaProjects/back-it-up/src/test/resources/unarchive/testOuputDir";

    private static final String TEST_DIRECTORY_TO_ARCHIVE = "/Users/Dave/IdeaProjects/back-it-up/src/test/resources/archive/testInputDir";
    private static final String TEST_ARCHIVE_OUTPUT_FILE = "/Users/Dave/IdeaProjects/back-it-up/src/test/resources/archive/testOuputFile.tar";


    private ArchiveService archiveService = new TarArchiveService();


    @Test
    public void archiveFile() {

        File testFile = new File(TEST_ARCHIVE_OUTPUT_FILE);
        archiveService.archiveFile(TEST_DIRECTORY_TO_ARCHIVE, testFile);
    }

    @Test
    public void unarchiveFile() {

        File testFile = new File(TEST_FILE_TO_UNARCHIVE);
        archiveService.unarchiveFile(testFile, TEST_UNARCHIVE_OUTPUT_DIRECTORY);
    }

}