package kz.springboot.SpringPookFinal.repositories;

import kz.springboot.SpringPookFinal.entities.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface BrandsRepository extends JpaRepository<Brands, Long> {

}
