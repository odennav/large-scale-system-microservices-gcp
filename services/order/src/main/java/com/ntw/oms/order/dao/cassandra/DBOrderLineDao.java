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

package com.ntw.oms.order.dao.cassandra;

import com.ntw.oms.order.dao.OrderDao;
import com.ntw.oms.order.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by anurag on 24/03/17.
 */
@Component("CQL")
public class DBOrderLineDao implements OrderDao {

    private static final Logger logger = LoggerFactory.getLogger(DBOrderLineDao.class);

    @Autowired(required = false)
    @Qualifier("orderCassandraOperations")
    private CassandraOperations cassandraOperations;

    @Autowired(required = false)
    @Qualifier("orderCqlTemplate")
    private CqlTemplate cqlTemplate;

    public CassandraOperations getCassandraOperations() {
        return cassandraOperations;
    }

    public void setCassandraOperations(CassandraOperations cassandraOperations) {
        this.cassandraOperations = cassandraOperations;
    }

    public CqlTemplate getCqlTemplate() {
        return cqlTemplate;
    }

    public void setCqlTemplate(CqlTemplate cqlTemplate) {
        this.cqlTemplate = cqlTemplate;
    }

    @Override
    public Order getOrder(String userId, String id) {
        StringBuilder cql = new StringBuilder("select * from orderline where userId='")
                                            .append(userId).append("' and id='"+id+"'");
        List<DBOrderLine> dbOrderLines = getCassandraOperations().
                select(cql.toString(), DBOrderLine.class);
        if (dbOrderLines == null || dbOrderLines.size() == 0) {
            logger.debug("No order lines found; userId={}", cql);
            return null;
        }
        Order order = DBOrderLine.getOrder(userId, id, dbOrderLines);
        logger.debug("Fetched order; context={}", order);
        return order;
    }

    @Override
    public List<Order> getOrders(String userId) {
        StringBuilder cql = new StringBuilder("select * from orderline where userId='")
                                            .append(userId).append("'");
        List<DBOrderLine> dbOrderLines = getCassandraOperations().
                select(cql.toString(), DBOrderLine.class);
        if (dbOrderLines == null || dbOrderLines.size() == 0) {
            logger.debug("No order lines found; userId={}", cql);
            return new LinkedList<>();
        }
        List<Order> orders = getOrders(dbOrderLines);
        logger.debug("Fetched orders; context={}", orders);
        return orders;
    }

    @Override
    public boolean saveOrder(Order order) {
        List<DBOrderLine> dbOrderLines = DBOrderLine.createDBOrder(order);
        for (DBOrderLine dbOrderLine : dbOrderLines) {
            DBOrderLine retDBOrderLine = null;
            try {
                retDBOrderLine = getCassandraOperations().insert(dbOrderLine);
            } catch (Exception e) {
                logger.error("Unable to save order; context={}", order);
                logger.error("Exception log:", e);
                return false;
            }
            if (retDBOrderLine == null) {
                logger.error("Unable to save order; context={}", order);
                return false;
            }
        }
        logger.debug("Saved order; context={}", order);
        return true;
    }

    @Override
    public boolean removeOrder(String userId, String id) {
        StringBuilder cql = new StringBuilder("delete from orderline where userId='")
                            .append(userId).append("' and id='").append(id).append("'");
        getCassandraOperations().delete(cql.toString());
        logger.debug("Removed order; orderId={}", id);
        return true;
    }

    @Override
    public boolean removeOrders() {
        String cql = "truncate orderline";
        cqlTemplate.execute(cql);
        logger.debug("Removed all orders");
        return true;
    }

    private List<Order> getOrders(List<DBOrderLine> dbOrderLines) {
        Map<String,List<DBOrderLine>> orderMap = new HashMap<>(100);
        for (DBOrderLine dbOrderLine : dbOrderLines) {
            String orderId = dbOrderLine.getOrderKey().getId();
            List<DBOrderLine> dbUserOrderLines = orderMap.get(orderId);
            if (dbUserOrderLines == null) {
                dbUserOrderLines = new LinkedList<>();
                orderMap.put(orderId, dbUserOrderLines);
            }
            dbUserOrderLines.add(dbOrderLine);
        }
        List<Order> orderList = new LinkedList<>();
        for (String orderId : orderMap.keySet()) {
            List<DBOrderLine> dbUserOrderLines = orderMap.get(orderId);
            Order order = DBOrderLine.getOrder(dbUserOrderLines.get(0).getOrderKey().getUserId(),
                    dbUserOrderLines.get(0).getOrderKey().getId(), dbUserOrderLines);
            orderList.add(order);
        }
        return orderList;
    }
}
