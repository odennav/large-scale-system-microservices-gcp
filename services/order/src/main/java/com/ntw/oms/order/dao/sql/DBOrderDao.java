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

package com.ntw.oms.order.dao.sql;

import com.ntw.oms.order.dao.OrderDao;
import com.ntw.oms.order.entity.Order;
import com.ntw.oms.order.entity.OrderLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 24/03/17.
 */
@Component("SQL")
public class DBOrderDao implements OrderDao {

    @Autowired(required = false)
    @Qualifier("orderJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DBOrderLineDao orderLineDao;

    private static final Logger logger = LoggerFactory.getLogger(DBOrderDao.class);

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String GET_ORDER_SQL = "select * from OrderMaster where id=?";

    @Override
    public Order getOrder(String userId, String id) {
        DBOrder dbOrder;
        try {
            dbOrder = jdbcTemplate.queryForObject(GET_ORDER_SQL, new Object[]{id},
                    new BeanPropertyRowMapper<>(DBOrder.class));
        } catch (Exception e) {
            logger.error("Exception while fetching order from db; id={}", id);
            logger.error("Exception message: ", e);
            return null;
        }
        if (dbOrder == null) {
            logger.debug("No order found for orderId {}", id);
            return null;
        }
        Order order = DBOrder.getOrder(dbOrder);
        List<OrderLine> orderLines = orderLineDao.getOrderLines(id);
        order.setOrderLines(orderLines);
        logger.debug("Fetched order; context={}", order);
        return order;
    }

    private static final String GET_ORDERS_SQL = "select * from OrderMaster where userId=?";

    @Override
    public List<Order> getOrders(String userId) {
        List<DBOrder> dbOrders;
        try {
            dbOrders = jdbcTemplate.query(GET_ORDERS_SQL, new Object[]{userId},
                    new BeanPropertyRowMapper<>(DBOrder.class));
        } catch (Exception e) {
            logger.error("Exception while fetching orders from db");
            logger.error("Exception message: ", e);
            return null;
        }
        if (dbOrders.isEmpty()) {
            logger.debug("No order lines found; userId={}", GET_ORDERS_SQL);
            return new LinkedList<>();
        }
        List<Order> orders = new LinkedList<>();
        for (DBOrder dbOrder : dbOrders) {
            Order order = DBOrder.getOrder(dbOrder);
            List<OrderLine> orderLines = orderLineDao.getOrderLines(order.getId());
            order.setOrderLines(orderLines);
            orders.add(order);
        }
        logger.debug("Fetched orders; context={}", orders);
        return orders;
    }

    private static final String ORDER_INSERT_SQL =
            "insert into OrderMaster " +
                "(id, userId, status, createdDate, createdTime) " +
                "values(?,?,?,?,?)";

    @Override
    public boolean saveOrder(Order order) {
        DBOrder dbOrder = DBOrder.createDBOrder(order);
        try {
            int retValue = jdbcTemplate.update(ORDER_INSERT_SQL,
                    new Object[]{dbOrder.getId(), dbOrder.getUserId(), dbOrder.getStatus(),
                            dbOrder.getCreatedDate(), dbOrder.getCreatedTime()});
            if (retValue <= 0) {
                logger.error("Unable to save order; context={}", order);
                return false;
            }
        } catch (Exception e) {
            logger.error("Unable to save order; context={}", order);
            logger.error("Exception message: {}", e);
            return false;
        }
        if (!orderLineDao.saveOrderLines(order.getId(), order.getOrderLines())) {
            logger.error("Unable to save order lines; context={}", order);
            return false;
        }
        logger.debug("Saved order; context={}", order);
        return true;
    }

    private static final String DELETE_ORDER_SQL = "delete from OrderMaster where id=?";

    @Override
    public boolean removeOrder(String userId, String id) {
        if (! orderLineDao.removeOrderLines(id)) {
            logger.error("Unable to remove orderlines for order id {}", id);
            return false;
        }
        try {
            int rowUpdated = jdbcTemplate.update(DELETE_ORDER_SQL, new Object[]{id});
            if (rowUpdated <= 0) {
                logger.warn("Unable to delete order; id={}", id);
            }
        } catch (Exception e) {
            logger.error("Unable to delete order; id={}", id);
            logger.error("Exception message: ", e);
            return false;
        }
        logger.debug("Removed order; orderId={}", id);
        return true;
    }

    private static final String DELETE_ORDERS_SQL = "delete from OrderMaster";

    @Override
    public boolean removeOrders() {
        if (! orderLineDao.removeOrdersLines()) {
            logger.error("Unable to remove orderlines");
            return false;
        }
        try {
            int rowsUpdated = jdbcTemplate.update(DELETE_ORDERS_SQL);
            if (rowsUpdated <= 0) {
                logger.warn("Unable to delete orders");
            }
        } catch (Exception e) {
            logger.error("Unable to delete orders");
            logger.error("Exception message: ", e);
            return false;
        }
        logger.debug("Removed all orders");
        return true;
    }

}
