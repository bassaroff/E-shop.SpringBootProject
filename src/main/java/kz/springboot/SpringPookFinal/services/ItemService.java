package kz.springboot.SpringPookFinal.services;

import kz.springboot.SpringPookFinal.entities.*;

import java.util.List;

public interface ItemService {

    Items addItem(Items item);

    List<Items> getItems();

    Items getItem(Long id);

    void deleteItem(Items item);

    Items saveItem(Items item);

    List<Items> getItemsByNameAndBrandPriceBetweenAsc(String name, Brands brand, double price1, double price2);

    List<Items> getItemsByNameAndBrandPriceBetweenDesc(String name, Brands brand, double price1, double price2);

    List<Items> getItemsOrderAsc(String name);

    List<Items> getItemsOrderDesc(String name);

    List<Items> getItemsByBrandId(Long id);

    List<Brands> getAllBrands();

    Brands addBrand(Brands brand);

    Brands saveBrand(Brands brand);

    Brands getBrand(Long id);

    void deleteBrand(Long id);

    List<Countries> getAllCountries();

    Countries addCountry(Countries country);

    Countries saveCountry(Countries country);

    Countries getCountry(Long id);

    void deleteCountry(Long id);

    List<Items> getItemsByNameContainingAndPriceBetweenDesc(String name, double priceFrom, double priceTo);

    List<Items> getItemsByNameContainingAndPriceBetweenAsc(String name, double priceFrom, double priceTo);

    List<Categories> getAllCategories();

    Categories getCategory(Long id);
    Categories saveCategory(Categories category);
    Categories addCategory(Categories category);
    void deleteCategory(Long id);


    Pictures addPicture(Pictures picture);
    List<Pictures> getAllPictures();
    Pictures getPicture(Long id);
    void deletePicture(Pictures picture);
    Pictures savePicture(Pictures picture);

    List<Pictures> getAllPicturesByItem(Items item);
}
