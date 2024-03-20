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

package com.ntw.oms.cart.dao;

import com.ntw.oms.cart.config.TestConfig;
import com.ntw.oms.order.dao.OrderDao;
import com.ntw.oms.order.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by anurag on 24/03/17.
 */
public class OrderMockDao implements OrderDao {

    private static final Logger logger = LoggerFactory.getLogger(OrderMockDao.class);
    @Override
    public Order getOrder(String userId, String id) {
        if (id.equals(TestConfig.TEST_ORDER_ID_1)) {
            return TestConfig.createOrder(TestConfig.TEST_ORDER_ID_1);
        }
        return null;
    }

    @Override
    public List<Order> getOrders(String userId) {
        List<Order> orders = new LinkedList<>();
        orders.add(TestConfig.createOrder(TestConfig.TEST_ORDER_ID_1));
        return orders;
    }

    @Override
    public boolean saveOrder(Order order) {
        return true;
    }

    @Override
    public boolean removeOrder(String userId, String orderId) {
        return true;
    }

    @Override
    public boolean removeOrders() {
        return false;
    }

}
