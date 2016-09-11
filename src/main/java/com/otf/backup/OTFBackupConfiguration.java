package com.otf.backup;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.otf.backup.client.BackupClient;
import com.otf.backup.client.dropbox.DropboxBackupClient;
import com.otf.backup.file.archive.ArchiveService;
import com.otf.backup.file.archive.TarArchiveService;
import com.otf.backup.file.compress.CompressionService;
import com.otf.backup.file.compress.GzipCompressionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Locale;

/**
 * @author David Hancock
 */
@Configuration
public class OTFBackupConfiguration {

    @Bean
    public BackupClient backupClient(CompressionService compressionService, ArchiveService archiveService, DbxClientV2 client) {
        return new DropboxBackupClient(compressionService, archiveService, client);
    }

    @Bean
    public DbxClientV2 client(DbxRequestConfig config, @Value("${accessToken}") String accessToken) {
        return new DbxClientV2(config, accessToken);
    }

    @Bean
    public DbxRequestConfig config() {
        return new DbxRequestConfig("dropbox/otf-backup", Locale.UK.toString());
    }

    @Bean
    public CompressionService compressionService() {
        return new GzipCompressionService();
    }

    @Bean
    public ArchiveService archiveService() {
        return new TarArchiveService();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
        return new PropertySourcesPlaceholderConfigurer();
    }

}
