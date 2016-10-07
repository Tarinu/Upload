package shrug.storage;

import org.apache.log4j.Logger;

public class StorageFileNotFoundException extends StorageException {
    private static final Logger logger = Logger.getLogger(StorageFileNotFoundException.class);

    public StorageFileNotFoundException(String message) {
        super(message);
        logger.info(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
        logger.info(message, cause);
    }
}