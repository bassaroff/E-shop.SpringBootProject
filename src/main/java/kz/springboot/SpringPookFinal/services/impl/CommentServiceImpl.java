package kz.springboot.SpringPookFinal.services.impl;

import kz.springboot.SpringPookFinal.entities.Items;
import kz.springboot.SpringPookFinal.entities.Comments;
import kz.springboot.SpringPookFinal.repositories.CommentsRepository;
import kz.springboot.SpringPookFinal.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentsRepository commentsRepository;

    @Override
    public List<Comments> getAllComments() {
        return commentsRepository.findAll();
    }

    @Override
    public List<Comments> getAllCommentsByItem(Items item) {
        return commentsRepository.getAllByItemOrderByAddedDateDesc(item);
    }

    @Override
    public Comments getCommentById(Long id) {
        return commentsRepository.getOne(id);
    }

    @Override
    public Comments addComment(Comments comment) {
        return commentsRepository.save(comment);
    }

    @Override
    public Comments saveComment(Comments comment) {
        return commentsRepository.save(comment);
    }

    @Override
    public void deleteComment(Comments comment) {
        commentsRepository.delete(comment);
    }
}
