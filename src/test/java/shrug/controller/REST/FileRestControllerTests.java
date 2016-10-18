package shrug.controller.REST;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.jdbc.core.JdbcTemplate;
import shrug.domain.File;
import shrug.services.file.FileService;
import shrug.storage.StorageFileNotFoundException;
import shrug.storage.StorageService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class FileRestControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StorageService storageService;
    
    @MockBean
    private FileService fileService;
    
    @MockBean
    private JdbcTemplate jdbcTemplate;
 /*
    @Test
    public void shouldListAllFiles() throws Exception {
        given(this.storageService.loadAll())
                .willReturn(Stream.of(Paths.get("first.jpg"), Paths.get("second.jpg")));
        //todo fix this, it doesn't add anything to db
        fileService.saveFile(new File("first", "jpg", "http://localhost/files:8080/first.jpg"));
        this.mvc.perform(get("/pictures"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("files",
                        Matchers.contains(fileService.getFileByFilename("first"), new File("second", "jpg", "http://localhost/files:8080/second.jpg"))));
    }

    @Test
    public void shouldSaveUploadedFile() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "test.txt", "text/plain", "Spring Framework".getBytes());
        //todo gives wrong httpservletrequest error
        this.mvc.perform(fileUpload("/upload").file(multipartFile))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/"));

        then(this.storageService).should().store(multipartFile, "asd");
    }

    @Test
    public void shouldntSaveUploadedFile() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "test.txt", "text/plain", "Spring Framework".getBytes());
        //todo gives wrong httpservletrequest error
        this.mvc.perform(fileUpload("/upload").file(multipartFile))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/"));

        then(this.storageService).should().store(multipartFile, "asd");
    }*/
    
    @Test
    public void should404WhenMissingFile() throws Exception {
        given(this.storageService.loadAsResource("test.jpg"))
                .willThrow(StorageFileNotFoundException.class);

        this.mvc.perform(get("/api/files/test.jpg"))
                .andExpect(status().isNotFound());
    }

}