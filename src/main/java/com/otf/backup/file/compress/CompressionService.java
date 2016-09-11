package com.otf.backup.file.compress;

import java.io.File;

/**
 * @author David Hancock
 */
public interface CompressionService {

    void compressFile(File fileToCompress, File outputFile);

    File decompressFile(File fileToDecompress, String outputDirectory);

}
