package com.example.myapplication.bean;

/**
 * 收货地址实体，支持多地址与默认地址。
 */
public class Address {
    private int id;
    private String name;
    private String phone;
    private String detail;
    private boolean isDefault;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    /**
     * 订单快照用的完整地址文本。
     */
    public String toOrderAddress() {
        return name + " " + phone + " " + detail;
    }
}
