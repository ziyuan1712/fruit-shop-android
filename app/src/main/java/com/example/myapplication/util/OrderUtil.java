package com.example.myapplication.util;

import com.example.myapplication.bean.CartItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单商品明细的 JSON 序列化 / 反序列化，以及配送费计算。
 */
public final class OrderUtil {
    public static final double FREE_DELIVERY_LIMIT = 49.0;
    public static final double DELIVERY_FEE = 3.0;

    private OrderUtil() {
    }

    public static String itemsToJson(List<CartItem> items) {
        JSONArray array = new JSONArray();
        for (CartItem item : items) {
            JSONObject object = new JSONObject();
            try {
                object.put("fruitId", item.getFruitId());
                object.put("name", item.getName());
                object.put("price", item.getPrice());
                object.put("count", item.getCount());
                object.put("img", item.getImg());
                object.put("unit", item.getUnit());
                object.put("shopId", item.getShopId());
            } catch (JSONException ignored) {
                // 基本类型写入不会失败，这里仅作防御
            }
            array.put(object);
        }
        return array.toString();
    }

    public static List<CartItem> parseItems(String json) {
        List<CartItem> list = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            return list;
        }
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                if (object == null) {
                    continue;
                }
                CartItem item = new CartItem();
                item.setFruitId(object.optInt("fruitId"));
                item.setName(object.optString("name", "时令鲜果"));
                item.setPrice(object.optDouble("price", 0));
                item.setCount(object.optInt("count", 1));
                item.setImg(object.optString("img", ""));
                item.setUnit(object.optString("unit", "份"));
                item.setShopId(object.optInt("shopId"));
                list.add(item);
            }
        } catch (JSONException ignored) {
            // 历史脏数据不阻塞订单列表展示
        }
        return list;
    }

    public static int totalCount(List<CartItem> items) {
        int count = 0;
        for (CartItem item : items) {
            count += item.getCount();
        }
        return count;
    }

    public static double goodsTotal(List<CartItem> items) {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public static double deliveryFee(double goodsTotal) {
        if (goodsTotal <= 0 || goodsTotal >= FREE_DELIVERY_LIMIT) {
            return 0;
        }
        return DELIVERY_FEE;
    }

    public static String summary(String detailJson) {
        List<CartItem> items = parseItems(detailJson);
        if (items.isEmpty()) {
            return "商品信息已失效";
        }
        if (items.size() == 1) {
            return items.get(0).getName() + " ×" + items.get(0).getCount();
        }
        return items.get(0).getName() + " 等 " + totalCount(items) + " 件商品";
    }
}
