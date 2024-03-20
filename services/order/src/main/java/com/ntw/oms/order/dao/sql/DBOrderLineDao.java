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
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by anurag on 24/03/17.
 */
@Component
public class DBOrderLineDao {

    @Autowired(required = false)
    @Qualifier("orderJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(DBOrderLineDao.class);

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String GET_ORDER_SQL = "select * from orderline where orderId=?";

    public List<OrderLine> getOrderLines(String id) {
        List<OrderLine> dbOrderLines;
        try {
            dbOrderLines = jdbcTemplate.query(GET_ORDER_SQL, new Object[]{id},
                    new BeanPropertyRowMapper<>(OrderLine.class));
        } catch (Exception e) {
            logger.error("Exception while fetching order from db; id={}", id);
            logger.error("Exception message: ", e);
            return null;
        }
        if (dbOrderLines.isEmpty()) {
            logger.debug("No order lines found; userId={}", GET_ORDER_SQL);
            return null;
        }
        return dbOrderLines;
    }

    private static final String ORDER_INSERT_SQL =
            "insert into OrderLine " +
                "(orderId, orderLineId, productId, quantity) " +
                "values(?,?,?,?)";

    public boolean saveOrderLines(String orderId, List<OrderLine> orderLines) {
        int[] updateStatusArr;
        try {
            updateStatusArr = jdbcTemplate.batchUpdate(ORDER_INSERT_SQL, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i)
                        throws SQLException {
                    ps.setString(1, orderId);
                    ps.setInt(2, orderLines.get(i).getId());
                    ps.setString(3, orderLines.get(i).getProductId());
                    ps.setFloat(4, orderLines.get(i).getQuantity());
                }

                @Override
                public int getBatchSize() {
                    return orderLines.size();
                }
            });
        } catch (Exception e) {
            logger.error("Unable to save order lines; context={}", orderLines);
            logger.error("Exception message: ", e);
            return false;
        }
        if (updateStatusArr.length == 0) {
            logger.error("Unable to save order lines; context={}", orderLines);
            return false;
        }
        logger.debug("Saved order lines; context={}", orderLines);
        return true;
    }

    private static final String DELETE_ORDER_SQL = "delete from orderline where orderId=?";

    public boolean removeOrderLines(String id) {
        try {
            jdbcTemplate.update(DELETE_ORDER_SQL, new Object[]{id});
        } catch (Exception e) {
            logger.error("Unable to delete order; id={}", id);
            logger.error("Exception message: ", e);
            return false;
        }
        logger.debug("Removed order; orderId={}", id);
        return true;
    }

    private static final String DELETE_ORDERS_SQL = "delete from orderline";

    public boolean removeOrdersLines() {
        try {
            jdbcTemplate.update(DELETE_ORDERS_SQL);
        } catch (Exception e) {
            logger.error("Unable to delete order lines");
            logger.error("Exception message: ", e);
            return false;
        }
        logger.debug("Removed all order lines");
        return true;
    }

}
