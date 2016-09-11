package com.djh.backup.file.archive;

import java.io.File;

/**
 * @author David Hancock
 */
public interface ArchiveService {

    void archiveFile(String directoryToArchive, File outputArchiveFile);

    void unarchiveFile(File inputArchiveFile, String directoryToArchive);

}
