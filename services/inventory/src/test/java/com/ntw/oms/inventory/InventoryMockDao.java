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

package com.ntw.oms.inventory;

import com.ntw.oms.inventory.dao.InventoryDao;
import com.ntw.oms.inventory.entity.Inventory;
import com.ntw.oms.inventory.entity.InventoryReservation;
import com.ntw.oms.inventory.entity.InventoryReservationLine;

import java.util.List;

/**
 * Created by anurag on 26/07/20.
 */
public class InventoryMockDao implements InventoryDao {

    private Inventory mockInventory;

    public InventoryMockDao() {
        mockInventory = new Inventory();
        mockInventory.setProductId("");
        mockInventory.setQuantity(0);
    }

    @Override
    public List<Inventory> getInventory() {
        return null;
    }

    @Override
    public Inventory getInventory(String productId) {
        if (mockInventory.getProductId().equals(productId)) {
            return mockInventory;
        }
        return null;
    }

    @Override
    public synchronized boolean updateInventory(Inventory inventory) {
        mockInventory = inventory;
        return true;
    }

    @Override
    public boolean insertInventory(Inventory inventory) {
        mockInventory = inventory;
        return true;
    }

    @Override
    public boolean deleteInventory() {
        return false;
    }

    @Override
    public boolean reserveInventory(InventoryReservation inventoryReservation) {
        List<InventoryReservationLine> invResLines = inventoryReservation.getInventoryReservationLines();
        if (invResLines.size() == 0)
            return true;
        InventoryReservationLine inventory = invResLines.get(0);
        if (mockInventory.getQuantity() >= inventory.getQuantity()) {
            mockInventory.setQuantity(mockInventory.getQuantity() - inventory.getQuantity());
            return true;
        }
        return false;
    }
}
