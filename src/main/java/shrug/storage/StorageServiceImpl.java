package shrug.storage;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import shrug.domain.File;
import shrug.services.file.FileService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService{

    private final Path rootLocation;
    private final FileService fileService;
    
    private static final Logger logger = Logger.getLogger(StorageServiceImpl.class);

    @Autowired
    public StorageServiceImpl(StorageProperties properties, FileService fileService) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.fileService = fileService;
    }
    
    @Override
    public void store(MultipartFile file, String location) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            logger.info("Generating new filename.");
            File newFile = new File(RandomStringUtils.randomAlphanumeric(File.nameLength),
                    file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')+1),
                    location);
            logger.info("New filename: " + newFile.getFilename()+"."+newFile.getType() + ". Trying to save it.");
            while(true) {
                try {
                    //todo sync saving the file and inserting to database
                    fileService.saveFile(newFile);
                    Files.copy(file.getInputStream(), rootLocation.resolve(newFile.getFilename()+"."+newFile.getType()));
                    break;
                } catch (DuplicateKeyException | FileAlreadyExistsException e) {
                    logger.info("Filename " + newFile.getFilename() + " already in use.");
                    newFile.setNewFilename(location);
                    logger.info("New filename: " + newFile.getFilename() + "." + newFile.getType() + ". Trying to save it.");
                }
            }
            logger.info("Saved " + newFile.getFilename()+"."+newFile.getType());
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException("Couldn't find file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Couldn't find file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
