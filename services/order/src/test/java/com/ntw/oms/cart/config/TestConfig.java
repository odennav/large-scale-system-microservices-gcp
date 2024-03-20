//////////////////////////////////////////////////////////////////////////////
// Copyright 2020 Anurag Yadav (anurag.yadav@newtechways.com)               //
//                                                                          //
// Licensed under the Apache License, Version 2.0 (the "License");          //
// you may not use this file except in compliance with the License.         //
// You may obtain a copy of the License at                                  //
//                                                                          //
//     http://www.apache.org/licenses/LICENSE-2.0                           //
//                                                                          //
// Unless required by applicable law or agreed to in writing, software      //
// distributed under the License is distributed on an "AS IS" BASIS,        //
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. //
// See the License for the specific language governing permissions and      //
// limitations under the License.                                           //
//////////////////////////////////////////////////////////////////////////////

package com.ntw.oms.cart.config;

import com.ntw.oms.cart.entity.Cart;
import com.ntw.oms.cart.entity.CartLine;
import com.ntw.oms.order.entity.Order;
import com.ntw.oms.order.entity.OrderLine;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 16/05/17.
 */
public class TestConfig {

    public static Cart createCart(String id) {
        Cart cart = new Cart(id);
        List<CartLine> cartLines = new LinkedList<>();
        cartLines.add(new CartLine(1,TEST_PRODUCT_ID_1,1));
        cartLines.add(new CartLine(2,TEST_PRODUCT_ID_2,1));
        cart.setCartLines(cartLines);
        return cart;
    }

    public static final String TEST_ORDER_ID_1 = "Test-Order-Id-1";
    public static final String TEST_PRODUCT_ID_1 = "Test-Product-Id-1";
    public static final String TEST_PRODUCT_ID_2 = "Test-Product-Id-2";
    public static final String TEST_PRODUCT_ID_3 = "Test-Product-Id-3";

    public static final String TEST_USER_ID = "anurag";

    public static Order createOrder(String id) {
        Order order = new Order(id, TEST_USER_ID);
        List<OrderLine> orderLines = new LinkedList<>();
        orderLines.add(new OrderLine(1,TEST_PRODUCT_ID_1,1));
        orderLines.add(new OrderLine(2,TEST_PRODUCT_ID_2,1));
        order.setOrderLines(orderLines);
        return order;
    }

}
