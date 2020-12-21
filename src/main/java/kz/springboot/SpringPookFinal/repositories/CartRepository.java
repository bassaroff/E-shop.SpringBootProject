package kz.springboot.SpringPookFinal.repositories;

import kz.springboot.SpringPookFinal.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CartRepository extends JpaRepository<Cart,Long> {

}
