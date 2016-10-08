package shrug.services.comment;

import shrug.domain.Comment;
import shrug.domain.File;

import java.util.List;

public interface CommentService {
    
    void saveComment(Comment comment);
    
    void saveCommentWithoutUsername(Comment comment);
    
    List<Comment> getAllPictureComments(File file);
}
