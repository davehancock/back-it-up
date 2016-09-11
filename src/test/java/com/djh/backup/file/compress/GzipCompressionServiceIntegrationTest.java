package com.djh.backup.file.compress;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/**
 * FIXME Make this a runnable integration test in CI
 *
 * @author David Hancock
 */
@Ignore
public class GzipCompressionServiceIntegrationTest {


    private static final String TEST_ARCHIVE_TO_COMPRESS = "/Users/Dave/IdeaProjects/back-it-up/src/test/resources/compress/testOuputFile.tar";
    private static final String TEST_COMPRESSION_OUTPUT_FILE = "/Users/Dave/IdeaProjects/back-it-up/src/test/resources/compress/testOuputFile.tar.gz";

    private CompressionService gzipCompressionService = new GzipCompressionService();

    @Test
    public void compressFile() {

        File archiveFile = new File(TEST_ARCHIVE_TO_COMPRESS);
        File outputCompressedFile = new File(TEST_COMPRESSION_OUTPUT_FILE);

        gzipCompressionService.compressFile(archiveFile, outputCompressedFile);
    }

    /**
     * TODO NO-OP
     */
    @Test
    public void decompressFile() {

    }

}