# 果悦鲜生 · 水果商店 Android 项目

以“网上订餐”项目为原型改写的校园水果商店 App，使用 **Java + AndroidX + Material 3** 实现。
项目名称、图标、配色、商品数据与界面布局均为自定义，可直接用 Android Studio 打开运行。

## 一、运行信息

| 项目 | 内容 |
| --- | --- |
| 应用名称 | 果悦鲜生 |
| applicationId | `com.example.myapplication` |
| minSdk / targetSdk / compileSdk | 24 / 37 / 37 |
| 版本 | 2.1（versionCode 3） |
| 开发语言 | Java 11 |
| 演示账号 | `admin` / `123456`（首次启动自动写入本地） |
| 启动入口 | `SplashActivity` → 已登录进入 `MainActivity`，否则进入 `LoginActivity` |

在 Android Studio 中打开项目，等待 Gradle 同步完成后运行 `app` 即可。
命令行构建：

```bash
./gradlew :app:assembleDebug
```

APK 输出：`app/build/outputs/apk/debug/app-debug.apk`

## 界面预览

| 启动页 | 登录页 | 首页 |
| --- | --- | --- |
| ![启动页](screenshots/01-splash.png) | ![登录页](screenshots/02-login.png) | ![首页](screenshots/03-home.png) |

| 店铺详情 | 水果详情 | 购物车 |
| --- | --- | --- |
| ![店铺详情](screenshots/04-shop-detail.png) | ![水果详情](screenshots/05-fruit-detail.png) | ![购物车](screenshots/06-cart.png) |

| 确认订单 | 下单成功 | 我的订单 | 个人中心 |
| --- | --- | --- | --- |
| ![确认订单](screenshots/07-order-confirm.png) | ![下单成功](screenshots/08-order-success.png) | ![我的订单](screenshots/09-my-orders.png) | ![个人中心](screenshots/10-profile.png) |

| 我的收藏 | 我的优惠券 | 评价订单 |
| --- | --- | --- |
| ![我的收藏](screenshots/11-favorites.png) | ![我的优惠券](screenshots/12-coupons.png) | ![评价订单](screenshots/13-review.png) |

| 评价完成 | 订单评价详情 |
| --- | --- |
| ![评价完成](screenshots/14-order-reviewed.png) | ![订单评价详情](screenshots/15-review-detail.png) |

| 收货地址管理 |
| --- |
| ![收货地址管理](screenshots/16-address.png) |

## 二、功能完成情况

### 必做模块

| 要求 | 实现页面 | 说明 |
| --- | --- | --- |
| 店铺页面 / 店铺列表 | `MainActivity` | RecyclerView 展示 4 家店铺，支持按店铺名、水果名搜索 |
| 店铺详情 + 菜品列表 | `ShopDetailActivity` | 店铺封面、简介、评分、配送时间、起送价，水果双列列表，支持快捷加购 |
| 菜品详情 + 加入购物车 | `FruitDetailActivity` | 大图、名称、价格、描述、库存、销量、数量选择、加入购物车 |
| 订单确认与提交 | `OrderActivity` | 收货地址、商品清单、备注、优惠券抵扣、商品金额 / 配送费 / 优惠 / 合计，提交后写入订单表并清空购物车 |

### 选做模块（已完成）

- **启动页**：品牌 Logo、Slogan、2 秒倒计时与“跳过”按钮，根据登录状态自动分流。
- **登录 / 注册**：用户名、密码、确认密码校验；账号保存在 SharedPreferences；注册成功后回到登录态，需手动输入密码登录；“记住登录状态”控制下次启动是否自动登录。
- **个人中心**：用户信息、订单数 / 购物车数 / 地址状态、我的订单、我的收藏、购物车、我的优惠券、收货地址、关于、退出登录。
- **搜索**：首页按店铺名、描述、标签、水果名、水果描述实时过滤。
- **收货地址管理**：独立的收货地址列表页，支持多地址新增、编辑、删除、设为默认；个人中心“收货地址”统计与列表行均可进入；确认订单页默认使用默认地址，也可临时选择其他地址，订单保存配送地址快照。
- **我的订单**：从 SQLite 读取历史订单，点击查看商品明细、备注、优惠券与评价。
- **购物车增强**：数量增减、删除、清空、空状态、金额实时计算；角标与个人中心按“商品种类”统计，同一种水果买多份只算 1 个。
- **收藏**：水果详情页工具栏爱心一键收藏；收藏写入 SQLite；“我的收藏”支持进入详情与取消收藏。
- **评价**：订单卡片“评价订单”打开星级 + 文字评价；评价写入 SQLite，订单状态更新为“已完成”；已评价订单可进入“修改评价”，详情回显星级与内容。
- **优惠券**：预置 3 张满减券；确认订单页按商品金额筛选可用券并抵扣；提交后标记已使用，“我的优惠券”展示待使用 / 已使用状态。
- **状态与异常处理**：首页加载中、加载失败重试、空数据、搜索无结果；购物车 / 订单 / 收藏 / 优惠券空状态；JSON 解析异常兜底。

题目列出的选做功能已全部覆盖。

## 三、技术要求对应

| 技术要求 | 实现位置 |
| --- | --- |
| Activity / Fragment 组织页面 | 12 个 Activity：启动、登录、首页、店铺、水果、购物车、订单确认、订单列表、收藏、优惠券、地址管理、个人中心 |
| ListView / RecyclerView 展示列表 | 首页店铺、店铺水果、购物车、订单、收藏、优惠券、收货地址均使用 RecyclerView（`ShopAdapter`、`FruitAdapter`、`CartAdapter`、`OrderAdapter`、`FavoriteAdapter`、`CouponAdapter`、`AddressAdapter`） |
| Intent 页面跳转与数据传递 | 店铺 / 水果对象实现 `Serializable`，通过 `Intent.putExtra` 在页面间传递；地址选择通过 `ActivityResultLauncher` 回传地址 ID；收藏与订单数据通过 SQLite 衔接 |
| SharedPreferences 保存登录状态、用户信息 | `SPUtil`：账号、当前用户、自动登录标记；旧版单地址在升级时迁移到 SQLite |
| SQLite 保存购物车、订单 | `DBHelper` + `CartDao`、`OrderDao`、`FavoriteDao`、`ReviewDao`、`CouponDao`、`AddressDao`，共 6 张业务表；订单明细以 JSON 存入订单表 |
| 解析本地或远程 JSON | `JsonUtil` 读取 `res/raw/shop_data.json`，使用 `JSONObject` / `JSONArray` 解析，字段缺失时使用 `opt` 默认值 |
| 异常处理与加载状态 | 首页子线程解析 + 加载中 / 失败重试 / 空数据；数据库与弹窗输入校验；图片资源安全回退 |

## 四、页面流程

```
SplashActivity
   ├─ 已登录且勾选记住登录 → MainActivity
   └─ 未登录 → LoginActivity（登录 / 注册）→ MainActivity
MainActivity
   ├─ 店铺卡片 → ShopDetailActivity
   │                ├─ 水果卡片 → FruitDetailActivity → 加入购物车 / 收藏
   │                └─ 购物车 / 去结算 → CartActivity
   ├─ 购物车 → CartActivity → OrderActivity（选择地址 / 优惠券）→ 提交成功
   │                                      ├─ 查看订单 → OrderListActivity
   │                                      └─ 继续逛逛 → MainActivity
   └─ 我的 → ProfileActivity
                    ├─ 我的订单 → OrderListActivity → 评价订单 → 已完成
                    ├─ 我的收藏 → FavoritesActivity → FruitDetailActivity
                    ├─ 购物车 → CartActivity
                    ├─ 我的优惠券 → CouponsActivity
                    ├─ 收货地址 → AddressActivity（多地址 / 默认地址）
                    └─ 退出登录 → LoginActivity
```

## 五、数据设计

### 本地 JSON（`app/src/main/res/raw/shop_data.json`）

```json
{
  "shopList": [
    {
      "shopId": 1,
      "shopName": "鲜果季·中央公园店",
      "shopDesc": "每日产地直采，2小时鲜活送达",
      "shopImg": "shop_1",
      "tags": "坏果包赔,产地直发",
      "rating": 4.9,
      "deliveryTime": "30分钟",
      "distance": "0.8km",
      "minOrder": 20,
      "fruitList": [
        {"fruitId": 101, "name": "阳光玫瑰葡萄", "price": 19.9, "desc": "...", "img": "fruit_grape", "stock": 50, "unit": "斤", "sales": 326}
      ]
    }
  ]
}
```

`shopImg` / `img` 对应 `drawable-nodpi` 中的图片名，`ResUtil` 会按名称查找，找不到时回退到 `fruit_placeholder`。

### SQLite（`fruit_shop.db`，版本 4）

- `cart`：`id`、`fruitId`（唯一）、`name`、`price`、`img`、`count`、`shopId`、`unit`
- `orders`：`id`、`orderNo`、`totalPrice`、`createTime`、`detail`（商品明细 JSON）、`status`、`itemCount`、`note`、`discount`、`couponTitle`、`address`（配送地址快照）
- `favorites`：`id`、`fruitId`（唯一）、`name`、`price`、`fruitDesc`、`img`、`stock`、`unit`、`sales`、`shopId`
- `reviews`：`id`、`orderNo`（唯一）、`rating`、`content`、`createTime`
- `coupons`：`id`、`name`、`threshold`、`amount`、`used`；首次建库预置 3 张演示券
- `addresses`：`id`、`name`、`phone`、`addressDetail`、`isDefault`；允许多条，默认地址至多一条

同一水果重复加入购物车时会累加数量，不会产生重复行；同一订单只保留一条评价；优惠券使用后 `used=1`，不再出现在可用列表中。数据库 v3 → v4 采用增量迁移，保留购物车、订单、收藏、评价与优惠券；旧版 SharedPreferences 单地址会自动迁移为一条默认地址。

## 六、目录结构

```
app/src/main/java/com/example/myapplication/
├── SplashActivity.java          启动页
├── LoginActivity.java           登录 / 注册
├── MainActivity.java            首页店铺列表 + 搜索 + 底部导航
├── ShopDetailActivity.java      店铺详情
├── FruitDetailActivity.java     水果详情
├── CartActivity.java            购物车
├── OrderActivity.java           订单确认 / 提交
├── OrderListActivity.java       我的订单 + 评价
├── FavoritesActivity.java       我的收藏
├── CouponsActivity.java         我的优惠券
├── AddressActivity.java         收货地址管理与选择
├── ProfileActivity.java         个人中心
├── BaseActivity.java            沉浸式状态栏 + SharedPreferences 初始化
├── adapter/                     ShopAdapter、FruitAdapter、CartAdapter、OrderAdapter、FavoriteAdapter、CouponAdapter、AddressAdapter
├── bean/                        Shop、Fruit、CartItem、Order、FavoriteItem、Review、Coupon、Address
├── db/                          DBHelper、CartDao、OrderDao、FavoriteDao、ReviewDao、CouponDao、AddressDao
└── util/                        JsonUtil、SPUtil、OrderUtil、MoneyUtil、ResUtil、DialogUtil

app/src/main/res/
├── layout/                      12 个 Activity 布局 + 列表项 / 空状态 / 地址、评价弹窗
├── drawable/                    图标、圆角背景、标签、启动页渐变
├── drawable-nodpi/              12 张水果插画、4 张店铺封面、Banner、占位图
├── mipmap-*/                    自定义启动图标（自适应 + 兼容图标）
├── menu/                        购物车“清空”、水果详情“收藏”菜单
├── raw/shop_data.json           本地店铺 / 商品数据
└── values/                      颜色、主题、样式、字符串
```

## 七、实现说明

- **图片资源**：项目不依赖网络图片，全部水果与店铺插画由本地脚本生成后放入 `drawable-nodpi`，离线可运行，也避免了图片版权问题。
- **状态栏适配**：`BaseActivity` 使用 `WindowInsetsCompat`，兼容 Android 15+ 强制沉浸式，登录 / 订单页会避让软键盘。
- **数据一致性**：旧版本中“加入购物车写 `fruitName`、读取 `name`”的字段错位问题已修复；数据库升级到 v4，新增多地址表与订单地址快照，v3 → v4 增量迁移保留原有数据。
- **业务闭环**：收藏 → 再次进入详情；优惠券选择 → 抵扣 → 标记已使用；订单评价 → 状态变更为“已完成” → 订单详情回显星级与内容；地址选择 → 默认地址 → 订单保存配送地址快照。
- **代码精简**：删除了未使用的 Kotlin `MainActivity`、示例测试、重复的 `fruit_data.json` 和旧版启动图标，避免 Java/Kotlin 混合与资源冲突。
- **容错**：JSON 字段缺失、图片缺失、数据库为空、地址未填写、库存为 0 等情况均有对应处理。
- **统计口径**：购物车角标和个人中心按 `cart` 表行数（商品种类）统计；购物车金额、订单明细仍按实际购买份数计算。

## 八、代码检查与已知取舍

本次代码审计已修复：

- Android Lint 从 **34 个 Error** 降为 **0 Error**（仍有 HardcodedText、SmallSp、Overdraw 等 UI/国际化类 Warning）。
- 全部布局的 `android:tint` 改为 `app:tint`，符合 AppCompat 要求。
- 移除低版本不支持的 `android:windowLightNavigationBar`，在 `BaseActivity` 中按 API 版本处理导航栏图标，API 24/25 使用深绿色导航栏。
- 启动页返回键改用 `OnBackPressedDispatcher`，兼容 Android 13+ 手势返回。
- 修复圆形启动图标透明区域被深绿背景填满的问题。
- 修复登录页 / 确认订单页旋转后状态丢失（注册模式、已选优惠券）。
- 修复无效店铺 / 商品 Intent 进入页面后 `onResume` 可能空指针的问题。
- 清理未使用导入、未使用 `KEY_REMEMBER`、冗余 `setData + setReviewed` 双刷新、重复的 `shopName` Extra。
- 店铺列表评分与配送文案补充 Locale / “达”字重复判断。

已知取舍（课程演示项目）：

- 数据库升级到 v4：v3 → v4 采用增量迁移，保留现有购物车 / 订单 / 收藏 / 评价 / 优惠券；v1 / v2 结构过旧，升级时仍会重建。
- 账号密码以明文存储在本地 SharedPreferences，仅用于课程演示，不适合真实生产环境。
- 购物车、收藏、优惠券、收货地址为设备级数据，未按账号隔离。
- 库存用于展示，未实现严格的下单扣减 / 超卖控制。
- Android Lint 剩余 Warning 主要为硬编码中文、过小字号、过度绘制和列表整体刷新，属于界面文案与性能优化项，不影响功能运行。
