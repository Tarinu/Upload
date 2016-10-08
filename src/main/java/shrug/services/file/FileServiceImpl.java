package shrug.services.file;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import shrug.domain.File;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class FileServiceImpl implements FileService{
    
    @Resource
    private JdbcTemplate jdbcTemplate;
    private static final Logger logger = Logger.getLogger(FileServiceImpl.class);
    
    @Override
    public List<File> getAllFiles() {
        String sql = "SELECT id, filename, type, location, created FROM files";
        logger.info("Query = " + sql);
        return jdbcTemplate.query(sql, new FileRowMapper());
    }
    
    @Override
    public File getFile(int id) {
        String sql = "SELECT id, filename, type, location, created FROM files WHERE id = ?";
        logger.info("Query = " + sql + "; id = " + id);
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new FileRowMapper());
    }
    
    @Override
    public File getFileByFilename(String filename) {
        String sql = "SELECT id, filename, type, location, created FROM files WHERE filename = ?";
        logger.info("Query = " + sql + "; filename = " + filename);
        return jdbcTemplate.queryForObject(sql, new Object[]{filename}, new FileRowMapper());
    }
    
    @Override
    public void saveFile(File file) {
        String sql = "INSERT INTO files(filename, type, location) VALUES (?, ?, ?)";
        logger.info("Query = " + sql);
        jdbcTemplate.update(sql, file.getFilename(), file.getType(), file.getLocation());
    }
}

class FileRowMapper implements RowMapper<File>{
    
    @Override
    public File mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new File(rs.getInt("id"),
                rs.getString("filename"),
                rs.getString("type"),
                rs.getString("location"),
                rs.getTimestamp("created"));
    }
}
