package kz.springboot.SpringPookFinal.repositories;


import kz.springboot.SpringPookFinal.entities.Brands;
import kz.springboot.SpringPookFinal.entities.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ItemRepository extends JpaRepository<Items, Long> {

    List<Items> findAllByNameContainingIgnoreCaseAndBrandAndPriceBetweenOrderByPriceAsc(String name, Brands brand, double price1, double price2);
    List<Items> findAllByNameContainingIgnoreCaseAndBrandAndPriceBetweenOrderByPriceDesc(String name, Brands brand, double price1, double price2);


    List<Items> findAllByNameContainingIgnoreCaseAndPriceBetweenOrderByPriceAsc(String name, double price1, double price2);
    List<Items> findAllByNameContainingIgnoreCaseAndPriceBetweenOrderByPriceDesc(String name, double price1, double price2);

    List<Items> findAllByNameContainingIgnoreCaseOrderByPriceAsc(String name);
    List<Items> findAllByNameContainingIgnoreCaseOrderByPriceDesc(String name);

    List<Items> findAllByBrand_Id(Long id);
//    List<Items> findAllByNameContainingIgnoreCaseAndBranAndPriceBetweenOrderByPriceAsc(String name, Brands brand, double price1, double price2);
//    List<Items> findAllByNameContainingIgnoreCaseAndBranAndPriceBetweenOrderByPriceDesc(String name, Brands brand, double price1, double price2);
}
