package kz.springboot.SpringPookFinal.services.impl;

import kz.springboot.SpringPookFinal.entities.*;
import kz.springboot.SpringPookFinal.repositories.*;
import kz.springboot.SpringPookFinal.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    
    @Autowired
    private PicturesRepository picturesRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BrandsRepository brandsRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Items> getItemsByNameAndBrandPriceBetweenAsc(String name, Brands brand, double price1, double price2) {
        return itemRepository.findAllByNameContainingIgnoreCaseAndBrandAndPriceBetweenOrderByPriceAsc(name, brand, price1, price2);
    }

    @Override
    public List<Items> getItemsByNameAndBrandPriceBetweenDesc(String name, Brands brand, double price1, double price2) {
        return itemRepository.findAllByNameContainingIgnoreCaseAndBrandAndPriceBetweenOrderByPriceDesc(name, brand, price1, price2);
    }

    @Override
    public List<Items> getItemsByNameContainingAndPriceBetweenDesc(String name, double priceFrom, double priceTo) {
        return itemRepository.findAllByNameContainingIgnoreCaseAndPriceBetweenOrderByPriceDesc(name, priceFrom, priceTo);
    }

    @Override
    public List<Items> getItemsByNameContainingAndPriceBetweenAsc(String name, double priceFrom, double priceTo) {
        return itemRepository.findAllByNameContainingIgnoreCaseAndPriceBetweenOrderByPriceAsc(name,priceFrom,priceTo);
    }

    @Override
    public List<Items> getItemsOrderAsc(String name) {
        return itemRepository.findAllByNameContainingIgnoreCaseOrderByPriceAsc(name);
    }

    @Override
    public List<Items> getItemsOrderDesc(String name) {
        return itemRepository.findAllByNameContainingIgnoreCaseOrderByPriceDesc(name);
    }

    @Override
    public Items addItem(Items item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Items> getItems() {
        return itemRepository.findAll();
    }

    @Override
    public Items getItem(Long id) {
        return itemRepository.getOne(id);
    }

    @Override
    public void deleteItem(Items item) {
        itemRepository.delete(item);
    }

    @Override
    public Items saveItem(Items item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Brands> getAllBrands() {
        return brandsRepository.findAll();
    }

    @Override
    public Brands addBrand(Brands brand) {
        return brandsRepository.save(brand);
    }

    @Override
    public Brands saveBrand(Brands brand) {
        return brandsRepository.save(brand);
    }

    @Override
    public Brands getBrand(Long id) {
        return brandsRepository.getOne(id);
    }

    @Override
    public void deleteBrand(Long id) {
        brandsRepository.deleteById(id);
    }

    @Override
    public List<Items> getItemsByBrandId(Long id) {
        return itemRepository.findAllByBrand_Id(id);
    }

    @Override
    public List<Countries> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Countries addCountry(Countries country) {
        return countryRepository.save(country);
    }

    @Override
    public Countries saveCountry(Countries country) {
        return countryRepository.save(country);
    }

    @Override
    public Countries getCountry(Long id) {
        return countryRepository.getOne(id);
    }

    @Override
    public void deleteCountry(Long id) {
        countryRepository.deleteById(id);
    }

    @Override
    public List<Categories> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Categories getCategory(Long id) {
        return categoryRepository.getOne(id);
    }

    @Override
    public Categories saveCategory(Categories category) {
        return categoryRepository.save(category);
    }

    @Override
    public Categories addCategory(Categories category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }


    @Override
    public Pictures addPicture(Pictures picture) {
        return picturesRepository.save(picture);
    }

    @Override
    public List<Pictures> getAllPictures() {
        return picturesRepository.findAll();
    }

    @Override
    public Pictures getPicture(Long id) {
        return picturesRepository.getOne(id);
    }

    @Override
    public void deletePicture(Pictures picture) {
        picturesRepository.delete(picture);
    }

    @Override
    public Pictures savePicture(Pictures picture) {
        return picturesRepository.save(picture);
    }

    @Override
    public List<Pictures> getAllPicturesByItem(Items item) {
        return picturesRepository.findAllByItem(item);
    }
}
