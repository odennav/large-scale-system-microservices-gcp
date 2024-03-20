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

package com.ntw.oms.inventory.dao.cql;

import com.ntw.oms.inventory.entity.Inventory;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Created by anurag on 27/07/20.
 */
@Table("Inventory")
public class DBInventory {
    @PrimaryKey
    private String productId;
    private float quantity;

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

    public static DBInventory createInventory(Inventory inventory) {
        DBInventory dbInventory = new DBInventory();
        dbInventory.setProductId(inventory.getProductId());
        dbInventory.setQuantity(inventory.getQuantity());
        return dbInventory;
    }

    public Inventory getInventory() {
        Inventory inventory = new Inventory();
        inventory.setProductId(getProductId());
        inventory.setQuantity(getQuantity());
        return inventory;
    }
    
}
