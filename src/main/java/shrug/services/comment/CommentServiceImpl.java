package shrug.services.comment;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import shrug.domain.Comment;
import shrug.domain.File;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    
    @Resource
    private JdbcTemplate jdbcTemplate;
    private static final Logger logger = Logger.getLogger(CommentServiceImpl.class);
    
    @Override
    public void saveComment(Comment comment) {
        String sql = "INSERT INTO comments(username, comment, picture_id) values (?, ?)";
        logger.info("Query = " + sql);
        jdbcTemplate.update(sql, comment.getUsername(), comment.getComment(), comment.getPicture_id());
    }
    
    @Override
    public void saveCommentWithoutUsername(Comment comment) {
        String sql = "INSERT INTO comments(comment, picture_id) values (?, ?)";
        logger.info("Query = " + sql);
        jdbcTemplate.update(sql, comment.getComment(), comment.getPicture_id());
    }
    
    @Override
    public List<Comment> getAllPictureComments(File file) {
        String sql = "SELECT * FROM comments WHERE picture_id = ?";
        logger.info("Query = " + sql + ", picture_id = " + file.getId());
        return jdbcTemplate.query(sql, new Object[]{file.getId()}, new CommentRowMapper());
    }
}

class CommentRowMapper implements RowMapper<Comment>{
    
    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Comment(rs.getInt("id"), rs.getString("username"), rs.getString("comment"),
                rs.getTimestamp("timestamp"), rs.getInt("picture_id"));
    }
}
