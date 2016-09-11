# back-it-up

[![Build Status](https://travis-ci.org/daves125125/back-it-up.svg?branch=master)](https://travis-ci.org/daves125125/back-it-up)


<h2>Dropbox</h2>


<h3>Backup</h3>

Run the following command to backup a file to Dropbox (including automatic secondary backup and cleanup):

```sh
docker run -v /path/to/backup/file:/backup/file -v /path/to/archive/directory daves125125/back-it-up backup \
    --accessToken=[DROPBOX_ACCESS_TOKEN] --remotePath=[FILE_PATH_IN_DROPBOX] \
    --backupFilename=/backup/[FILE_NAME] --archiveDirectory=/backup/[ARCHIVE_DIRECTORY] \
```


<h3>Restore</h3>

Run the following command to restore a backup from Dropbox:

```sh
docker run -v /path/to/restore/directory:/restore daves125125/back-it-up restore \
    --accessToken=[DROPBOX_ACCESS_TOKEN] --filename=/restore/[FILE_NAME] --remotePath=[FILE_PATH_IN_DROPBOX]
```


<hr>