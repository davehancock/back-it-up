package com.otf.backup;

import com.otf.backup.client.BackupClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.inject.Inject;
import java.util.List;

/**
 * @author David Hancock
 */
@SpringBootApplication
public class BackupLoader implements ApplicationRunner {

    private static final String RESTORE_COMMAND = "restore";

    private static final String BACKUP_COMMAND = "backup";

    private static final String AGE_COMMAND = "age";

    private static final String DELETE_COMMAND = "delete";

    @Inject
    private BackupClient backupClient;

    @Value("${remotePath}")
    private String remotePath;

    // TODO Make these optional / pass in as command line args instead below
    // FIXME Default this for now to allow interoperability
    @Value("${backupFilename:foo}")
    private String backupFilename;

    // TODO Make these optional / pass in as command line args instead below
    // FIXME Default this for now to allow interoperability
    @Value("${outputDirectory:bar}")
    private String outputDirectory;

    // TODO Make these optional / pass in as command line args instead below
    // FIXME Default this for now to allow interoperability
    @Value("${archiveDirectory:car}")
    private String archiveDirectory;


    public static void main(String[] args) {
        SpringApplication.run(BackupLoader.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<String> nonOptionArgs = args.getNonOptionArgs();

        if (nonOptionArgs.size() != 1 || (!nonOptionArgs.contains(RESTORE_COMMAND)
                && !nonOptionArgs.contains(BACKUP_COMMAND)
                && !nonOptionArgs.contains(AGE_COMMAND)
                && !nonOptionArgs.contains(DELETE_COMMAND))) {

            throw new RuntimeException("One of either 'backup', 'restore' or 'age' command should be provided!");
        }

        if (nonOptionArgs.contains(RESTORE_COMMAND)) {
            backupClient.restoreFile(outputDirectory, remotePath);

        } else if (nonOptionArgs.contains(BACKUP_COMMAND)) {
            backupClient.backupFile(archiveDirectory, backupFilename, remotePath);

        } else if (nonOptionArgs.contains(AGE_COMMAND)) {
            backupClient.retrieveAgeOfFile(outputDirectory, remotePath);

        } else if (nonOptionArgs.contains(DELETE_COMMAND)) {
            backupClient.deleteFile(remotePath);
        }
    }

}
