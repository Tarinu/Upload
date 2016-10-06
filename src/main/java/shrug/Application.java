package shrug;

import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import shrug.storage.StorageException;
import shrug.storage.StorageProperties;
import shrug.storage.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Application extends SpringBootServletInitializer{
    
    private Logger logger = Logger.getLogger(Application.class);
    
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {try{
            storageService.init();
        }catch (StorageException e){
            logger.info("Directory already exists");
        }
	};
    }
}
