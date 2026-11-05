package com.example.myapplication.bean;

/**
 * 订单评价，对应 SQLite reviews 表（一个订单一条评价）。
 */
public class Review {
    private int id;
    private String orderNo;
    private int rating;
    private String content;
    private String createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * 将 1~5 星转为“★★★★★”用于弹窗展示。
     */
    public String getStars() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            builder.append(i < rating ? "★" : "☆");
        }
        return builder.toString();
    }
}
