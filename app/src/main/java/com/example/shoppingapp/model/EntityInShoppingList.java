package com.example.shoppingapp.model;

public class EntityInShoppingList {

    private String item;
    private String note;
    private String id;
    private double chf;

    // required for Firebase's automatic data mapping
    public EntityInShoppingList() {
    }

    public EntityInShoppingList(String item, double chf, String note, String id) {
        this.item = item;
        this.chf = chf;
        this.note = note;
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getChf() {
        return chf;
    }

    public void setChf(double chf) {
        this.chf = chf;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
