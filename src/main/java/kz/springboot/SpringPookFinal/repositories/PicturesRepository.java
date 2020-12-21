package kz.springboot.SpringPookFinal.repositories;


import kz.springboot.SpringPookFinal.entities.Items;
import kz.springboot.SpringPookFinal.entities.Pictures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PicturesRepository extends JpaRepository<Pictures,Long> {
    List<Pictures> findAllByItem(Items item);
}