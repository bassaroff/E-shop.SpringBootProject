package kz.springboot.SpringPookFinal.services.impl;

import kz.springboot.SpringPookFinal.entities.SoldItems;
import kz.springboot.SpringPookFinal.repositories.SoldItemRepository;
import kz.springboot.SpringPookFinal.services.SoldItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SoldItemsServiceImpl implements SoldItemsService {

    @Autowired
    private SoldItemRepository soldItemRepository;

    @Override
    public SoldItems addItem(SoldItems item) {
        return soldItemRepository.save(item);
    }

    @Override
    public List<SoldItems> getItems() {
        return soldItemRepository.findAll();
    }

    @Override
    public SoldItems getItem(Long id) {
        return soldItemRepository.getOne(id);
    }

    @Override
    public void deleteItem(SoldItems item) {
        soldItemRepository.delete(item);
    }

    @Override
    public SoldItems saveItem(SoldItems item) {
        return soldItemRepository.save(item);
    }

    @Override
    public List<SoldItems> getItemsByCart(Long cart_id) {
        return soldItemRepository.findAllByCart(cart_id);
    }
}
