package shrug.services;

import shrug.domain.File;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FileServiceImpl implements FileService{
    
    @Resource
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public List<File> getAllFiles() {
        String sql = "SELECT id, filename, location FROM files";
        return jdbcTemplate.query(sql, ((rs, rowNum) ->
                new File(rs.getInt("id"), rs.getString("filename"), rs.getString("location"))));
    }
    
    @Override
    public File getFile(int id) {
        String sql = "SELECT id, filename, location FROM files WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, ((rs, rowNum) ->
                new File(rs.getInt("id"), rs.getString("filename"), rs.getString("location"))));
    }
    
    @Override
    public void saveFile(File file) {
        String sql = "INSERT INTO files(filename, location) VALUES (?, ?)";
        jdbcTemplate.update(sql, file.getFilename(), file.getLocation());
    }
}
