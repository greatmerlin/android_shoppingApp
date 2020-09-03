package com.example.shoppingapp.persistence;

import com.example.shoppingapp.model.EntityInShoppingList;

public interface DataClickedListener {
    void dataClicked(EntityInShoppingList entityInShoppingList, String key);
}
