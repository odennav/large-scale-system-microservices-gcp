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

import com.ntw.oms.order.entity.Order;

import java.sql.Date;
import java.sql.Time;

/**
 * Created by anurag on 19/04/17.
 */
public class DBOrder {

    private String id;
    private String userId;
    private String status;
    private Date createdDate;
    private Time createdTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Time getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Time createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + (id == null ? "null" : "\"" + id + "\"") + ", " +
                "\"userId\":" + (userId == null ? "null" : "\"" + userId + "\"") + ", " +
                "\"status\":" + (status == null ? "null" : "\"" + status + "\"") + ", " +
                "\"createdDate\":" + (createdDate == null ? "null" : createdDate) + ", " +
                "\"createdTime\":" + (createdTime == null ? "null" : createdTime) +
                "}";
    }

    public static DBOrder createDBOrder(Order order) {
        DBOrder dbOrder = new DBOrder();
        dbOrder.setUserId(order.getUserId());
        dbOrder.setId(order.getId());
        dbOrder.setStatus(order.getStatus().toString());
        if (order.getCreatedDate() != null) {
            dbOrder.setCreatedDate(new Date(order.getCreatedDate().getTime()));
            dbOrder.setCreatedTime(new Time(order.getCreatedDate().getTime()));
        }
        return dbOrder;
    }

    public static Order getOrder(DBOrder dbOrder) {
        Order order = new Order();
        order.setId(dbOrder.getId());
        order.setUserId(dbOrder.getUserId());
        order.setStatus(dbOrder.getStatus().toString());
        long time = 0;
        if (dbOrder.getCreatedDate() != null)
            time += dbOrder.getCreatedDate().getTime();
        if (dbOrder.getCreatedTime() != null)
            time += dbOrder.getCreatedTime().getTime();
        order.setCreatedDate(new java.util.Date(time));
        return order;
    }
}
