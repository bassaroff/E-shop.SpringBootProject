package kz.springboot.SpringPookFinal.repositories;

import kz.springboot.SpringPookFinal.entities.Comments;
import kz.springboot.SpringPookFinal.entities.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CommentsRepository extends JpaRepository<Comments, Long> {
    List<Comments> getAllByItemOrderByAddedDateDesc(Items item);
}
