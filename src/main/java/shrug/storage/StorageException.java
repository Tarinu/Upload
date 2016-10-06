package shrug.storage;

import org.apache.log4j.Logger;

public class StorageException extends RuntimeException {
    Logger logger = Logger.getLogger(StorageException.class);

    public StorageException(String message) {
        super(message);
        logger.info(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
        logger.info(message + ", " + cause);
    }
}
