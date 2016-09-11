package com.djh.backup.file.compress;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author David Hancock
 */
public class GzipCompressionService implements CompressionService {

    private static final Logger LOG = LoggerFactory.getLogger(GzipCompressionService.class);


    @Override
    public void compressFile(File fileToCompress, File outputFile) {

        LOG.info("Compressing File: '{}' using Gzip to: '{}'", fileToCompress.getAbsolutePath(), outputFile.getAbsolutePath());

        // Setup the streams within a try with resources - we bundle all together here for simplicity,
        // but could split these out if needed.
        try (FileInputStream inputStream = new FileInputStream(fileToCompress);
             FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
             GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(fileOutputStream)) {

            IOUtils.copy(inputStream, gzOut);

        } catch (IOException e) {
            throw new RuntimeException("Compression of the file failed", e);
        }

        LOG.info("File Compression was successful, Path is: '{}'", outputFile.getAbsolutePath());
    }

    @Override
    public File decompressFile(File fileToDecompress, String outputDirectory) {

        LOG.info("Decompressing File: '{}' using Gzip to: '{}'", fileToDecompress.getAbsolutePath(), outputDirectory);

        String unGzippedFilePath = fileToDecompress.getAbsolutePath().replace(".gz", "");
        File decompressedFile = new File(unGzippedFilePath);

        // Setup the streams within a try with resources - we bundle all together here for simplicity,
        // but could split these out if needed.
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileToDecompress));
             GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
             FileOutputStream out = new FileOutputStream(decompressedFile)) {

            IOUtils.copy(gzIn, out);

        } catch (IOException e) {
            throw new RuntimeException("Decompression of the file failed", e);
        }

        LOG.info("File decompression was successful, Path is: '{}'", decompressedFile.getAbsolutePath());

        return decompressedFile;
    }

}
