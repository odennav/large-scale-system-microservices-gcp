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

package com.ntw.oms.cart.service;

import com.ntw.oms.cart.config.TestConfig;
import com.ntw.oms.cart.dao.CartMockDao;
import com.ntw.oms.cart.dao.OrderMockDao;
import com.ntw.oms.cart.entity.Cart;
import com.ntw.oms.order.dao.OrderDao;
import com.ntw.oms.order.entity.Order;
import com.ntw.oms.order.processor.OrderPostProcessor;
import com.ntw.oms.order.processor.OrderPreProcessor;
import com.ntw.oms.order.service.OrderServiceImpl;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;

/**
 * Created by anurag on 12/05/17.
 */
public class OrderServiceImplTest extends TestCase {

    private OrderServiceImpl orderService;

    public OrderServiceImplTest(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( OrderServiceImplTest.class );
    }

    public void setUp() throws Exception {
        super.setUp();
        orderService = new OrderServiceImpl();
        CartServiceImpl cartService = new CartServiceImpl();
        cartService.setCartDaoBean(new CartMockDao());
        orderService.setCartServiceBean(cartService);
        OrderPreProcessor orderPreProcessor = new OrderPreProcessor();
        OrderPostProcessor orderPostProcessor = new OrderPostProcessor();
        orderPostProcessor.setInventoryClientBean(new InventoryMockClient());
        orderService.setOrderPreProcessor(orderPreProcessor);
        orderService.setOrderPostProcessor(orderPostProcessor);
        OrderDao orderDao = new OrderMockDao();
        orderService.setOrderDaoBean(orderDao);
        orderService.getOrderPostProcessor().setOrderDaoBean(orderDao);
    }

    public void testDummy() {
        Assert.assertEquals(true, true);
    }

    public void testCreateOrder() {
        Order order = TestConfig.createOrder(TestConfig.TEST_ORDER_ID_1);
        boolean success = orderService.saveOrder(order);
        Assert.assertEquals(true, success);
    }

    public void testFetchOrder() {
        // Fetch order
        Order order = orderService.fetchOrder(TestConfig.TEST_USER_ID, TestConfig.TEST_ORDER_ID_1);
        Assert.assertNotNull(order);
        Assert.assertEquals(TestConfig.TEST_ORDER_ID_1, order.getId());
        Assert.assertEquals(order.getOrderLines().size(), order.getOrderLines().size());
        Assert.assertEquals(order.getOrderLines().get(0).getProductId(),
                order.getOrderLines().get(0).getProductId());
        Assert.assertEquals(order.getOrderLines().get(1).getProductId(),
                order.getOrderLines().get(1).getProductId());
        Assert.assertEquals(order.getOrderLines().get(0).getQuantity(),
                order.getOrderLines().get(0).getQuantity());
        Assert.assertEquals(order.getOrderLines().get(1).getQuantity(),
                order.getOrderLines().get(1).getQuantity());

        // Fetch order
        Order order1 = orderService.fetchOrder(TestConfig.TEST_USER_ID, "Non-Existent-Id");
        Assert.assertNull(order1);
        //Assert.assertEquals(0, order1.getOrderLines().size());
    }

    public void testRemoveOrder() {
        // Remove order
        boolean success = orderService.removeOrder(TestConfig.TEST_USER_ID, TestConfig.TEST_ORDER_ID_1);
        Assert.assertEquals(true, success);
    }

    public void testCreateOrderFromCart() throws IOException {
        Cart cart = TestConfig.createCart(TestConfig.TEST_USER_ID);
        Order order = orderService.createOrder(TestConfig.TEST_USER_ID, "null");
        Assert.assertNotNull(order);
        Assert.assertNotNull(order.getId());
        Assert.assertEquals(TestConfig.TEST_USER_ID, order.getUserId());
        Assert.assertEquals(cart.getCartLines().size(), order.getOrderLines().size());
        // Remove order
        boolean success = orderService.removeOrder(TestConfig.TEST_USER_ID, TestConfig.TEST_ORDER_ID_1);
        Assert.assertEquals(true, success);
    }


}
