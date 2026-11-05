package com.example.myapplication.bean;

/**
 * 购物车条目（对应 SQLite 中的 cart 表）。
 */
public class CartItem {
    private int id;
    private int fruitId;
    private String name;
    private double price;
    private String img;
    private int shopId;
    private int count;
    private String unit = "份";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFruitId() {
        return fruitId;
    }

    public void setFruitId(int fruitId) {
        this.fruitId = fruitId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getSubtotal() {
        return price * count;
    }
}
