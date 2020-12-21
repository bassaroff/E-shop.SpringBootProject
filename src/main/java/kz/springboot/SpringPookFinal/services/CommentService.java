package kz.springboot.SpringPookFinal.services;

import kz.springboot.SpringPookFinal.entities.Items;
import kz.springboot.SpringPookFinal.entities.Comments;

import java.util.List;

public interface CommentService {
    List<Comments> getAllComments();
    Comments getCommentById(Long id);
    Comments addComment(Comments comment);
    Comments saveComment(Comments comment);
    void deleteComment(Comments comment);
    List<Comments> getAllCommentsByItem(Items item);
}
