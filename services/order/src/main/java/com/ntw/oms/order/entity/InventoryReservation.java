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

package com.ntw.oms.order.entity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 12/05/19.
 * DTO class to send and receive inventory reservation request
 */
public class InventoryReservation {
    private List<InventoryReservationLine> inventoryReservationLines;

    public InventoryReservation() {
        this.inventoryReservationLines = new LinkedList<>();
    }

    public List<InventoryReservationLine> getInventoryReservationLines() {
        return inventoryReservationLines;
    }

    public void setInventoryReservationLines(List<InventoryReservationLine> inventoryReservationLines) {
        this.inventoryReservationLines = inventoryReservationLines;
    }

    public void addInvResLine(String productId, float quantity) {
        inventoryReservationLines.add(new InventoryReservationLine(productId, quantity));
    }

    @Override
    public String toString() {
        return "{" +
                "\"invResReqLines\":" + (inventoryReservationLines == null ?
                "null" : Arrays.toString(inventoryReservationLines.toArray())) +
                "}";
    }
}

class InventoryReservationLine {
    String productId;
    float quantity;

    public InventoryReservationLine(String productId, float quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "{" +
                "\"productId\":" + (productId == null ? "null" : "\"" + productId + "\"") + ", " +
                "\"quantity\":\"" + quantity + "\"" +
                "}";
    }
}
