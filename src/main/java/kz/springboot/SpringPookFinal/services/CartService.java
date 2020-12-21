package kz.springboot.SpringPookFinal.services;

import kz.springboot.SpringPookFinal.entities.Cart;

import java.util.List;

public interface CartService {
    Cart addCart(Cart cart);

    List<Cart> getCarts();

    Cart getCart(Long id);

    void deleteCart(Cart cart);

    Cart saveCart(Cart cart);
}
