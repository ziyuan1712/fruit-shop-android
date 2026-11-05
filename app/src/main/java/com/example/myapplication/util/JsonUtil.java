package com.example.myapplication.util;

import android.content.Context;

import com.example.myapplication.R;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.bean.Shop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析本地 JSON 数据（res/raw/shop_data.json）。
 * 使用 try-with-resources 与 opt 系列方法，字段缺失时也能安全降级。
 */
public final class JsonUtil {

    private JsonUtil() {
    }

    public static List<Shop> loadShops(Context context) throws IOException, JSONException {
        try (InputStream inputStream = context.getResources().openRawResource(R.raw.shop_data);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            String json = outputStream.toString(StandardCharsets.UTF_8.name());
            return parseShops(json);
        }
    }

    public static List<Shop> parseShops(String json) throws JSONException {
        List<Shop> shopList = new ArrayList<>();
        JSONObject root = new JSONObject(json);
        JSONArray shopArray = root.optJSONArray("shopList");
        if (shopArray == null) {
            throw new JSONException("缺少 shopList 字段");
        }
        for (int i = 0; i < shopArray.length(); i++) {
            JSONObject shopObject = shopArray.optJSONObject(i);
            if (shopObject == null) {
                continue;
            }
            Shop shop = new Shop();
            shop.setShopId(shopObject.optInt("shopId", i + 1));
            shop.setShopName(shopObject.optString("shopName", "未命名店铺"));
            shop.setShopDesc(shopObject.optString("shopDesc", ""));
            shop.setShopImg(shopObject.optString("shopImg", ""));
            shop.setTags(shopObject.optString("tags", ""));
            shop.setRating(shopObject.optDouble("rating", 4.8));
            shop.setDeliveryTime(shopObject.optString("deliveryTime", "30分钟"));
            shop.setDistance(shopObject.optString("distance", ""));
            shop.setMinOrder(shopObject.optDouble("minOrder", 0));
            shop.setFruitList(parseFruits(shopObject.optJSONArray("fruitList")));
            shopList.add(shop);
        }
        return shopList;
    }

    private static List<Fruit> parseFruits(JSONArray fruitArray) {
        List<Fruit> fruits = new ArrayList<>();
        if (fruitArray == null) {
            return fruits;
        }
        for (int i = 0; i < fruitArray.length(); i++) {
            JSONObject object = fruitArray.optJSONObject(i);
            if (object == null) {
                continue;
            }
            Fruit fruit = new Fruit();
            fruit.setFruitId(object.optInt("fruitId", i + 1));
            fruit.setName(object.optString("name", "时令鲜果"));
            fruit.setPrice(object.optDouble("price", 0));
            fruit.setDesc(object.optString("desc", ""));
            fruit.setImg(object.optString("img", ""));
            fruit.setStock(object.optInt("stock", 0));
            fruit.setUnit(object.optString("unit", "份"));
            fruit.setSales(object.optInt("sales", 0));
            fruits.add(fruit);
        }
        return fruits;
    }
}
