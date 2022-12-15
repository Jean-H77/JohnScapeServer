package com.ruse.model.container.impl.shopImpl;

import com.ruse.model.ShopItem;
import com.ruse.model.container.impl.Shop;

public class TestShop extends Shop {
    public TestShop() {
        super("Test shop", 995, true, true, false, new ShopItem(4151,1500, 1510000000),
                new ShopItem(6585,3750, 15),new ShopItem(1363,5000, 100),
                new ShopItem(1391,6000, 150),
                new ShopItem(11724,7000, 125),
                new ShopItem(11726,7000, 225),
                new ShopItem(11694,7000, 100),
                new ShopItem(11718,7000, 100),
                new ShopItem(11720,7000, 100),
                new ShopItem(11722,7000, 100),
                new ShopItem(14484,7000, 1510000000));
    }
}
