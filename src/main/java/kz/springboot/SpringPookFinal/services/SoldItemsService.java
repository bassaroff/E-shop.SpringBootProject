package kz.springboot.SpringPookFinal.services;

import kz.springboot.SpringPookFinal.entities.SoldItems;

import java.util.List;

public interface SoldItemsService {
    SoldItems addItem(SoldItems item);

    List<SoldItems> getItems();

    SoldItems getItem(Long id);

    void deleteItem(SoldItems item);

    SoldItems saveItem(SoldItems item);

    List<SoldItems> getItemsByCart(Long cart_id);

}
