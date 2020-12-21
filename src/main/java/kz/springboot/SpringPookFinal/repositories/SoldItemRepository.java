package kz.springboot.SpringPookFinal.repositories;

import kz.springboot.SpringPookFinal.entities.SoldItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface SoldItemRepository extends JpaRepository<SoldItems, Long> {
    List<SoldItems> findAllByCart(Long cart_id);
}
