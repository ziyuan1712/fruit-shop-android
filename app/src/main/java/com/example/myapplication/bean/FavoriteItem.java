package com.example.myapplication.bean;

/**
 * 收藏条目，对应 SQLite favorites 表。
 */
public class FavoriteItem {
    private int id;
    private int fruitId;
    private String name;
    private double price;
    private String desc;
    private String img;
    private int stock;
    private String unit;
    private int sales;
    private int shopId;

    public Fruit toFruit() {
        Fruit fruit = new Fruit();
        fruit.setFruitId(fruitId);
        fruit.setName(name);
        fruit.setPrice(price);
        fruit.setDesc(desc);
        fruit.setImg(img);
        fruit.setStock(stock);
        fruit.setUnit(unit);
        fruit.setSales(sales);
        return fruit;
    }

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }
}
