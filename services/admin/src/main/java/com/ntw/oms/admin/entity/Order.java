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

package com.ntw.oms.admin.entity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 19/04/17.
 */

public class Order {
    private String id;
    private String userId;
    private List<OrderLine> orderLines;

    public Order() {
        this.orderLines = new LinkedList<>();
    }

    public Order(String id, String userId) {
        this.id = id;
        this.userId = userId;
        this.orderLines = new LinkedList<>();
    }

    public Order(Order orderEntity) {
        this.id = orderEntity.id;
        this.userId = orderEntity.userId;
        this.orderLines = orderEntity.orderLines;
    }

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

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + (id == null ? "null" : "\"" + id + "\"") + ", " +
                "\"userId\":" + (userId == null ? "null" : "\"" + userId + "\"") + ", " +
                "\"orderLines\":" + (orderLines == null ? "null" : Arrays.toString(orderLines.toArray())) +
                "}";
    }
}
