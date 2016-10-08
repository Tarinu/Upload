package shrug.storage;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import shrug.domain.File;
import shrug.services.FileService;

import java.io.IOException;
import java.net.MalformedURLException;
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
            logger.info("Checking if file is empty.");
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            logger.info("Generating new filename.");
            String newname = RandomStringUtils.randomAlphanumeric(8);
            String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')+1);
            String fullname = newname+"."+type;
            
            logger.info("New filename: " + fullname + ". Trying to save it.");
            Files.copy(file.getInputStream(), this.rootLocation.resolve(fullname));
            fileService.saveFile(new File(newname, type, location + "/files/" + fullname));
            logger.info("Saved " + fullname);
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
                throw new StorageFileNotFoundException("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
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
