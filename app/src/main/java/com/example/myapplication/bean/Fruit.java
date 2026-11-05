package com.example.myapplication.bean;

import java.io.Serializable;

/**
 * 水果商品实体，实现 Serializable 以便通过 Intent 在页面之间传递。
 */
public class Fruit implements Serializable {
    private static final long serialVersionUID = 1L;

    private int fruitId;
    private String name;
    private double price;
    private String desc;
    private String img;
    private int stock;
    private String unit = "份";
    private int sales;

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
}
