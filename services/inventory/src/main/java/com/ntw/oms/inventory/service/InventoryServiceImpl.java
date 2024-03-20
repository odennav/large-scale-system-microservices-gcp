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

package com.ntw.oms.inventory.service;

import com.ntw.oms.inventory.dao.InventoryDao;
import com.ntw.oms.inventory.dao.InventoryDaoFactory;
import com.ntw.oms.inventory.entity.Inventory;
import com.ntw.oms.inventory.entity.InventoryReservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;

/**
 * Created by anurag on 12/05/17.
 */

/**
 * Provides implementation of Inventory Service methods
 */
@Component
public class InventoryServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    @Autowired
    private InventoryDaoFactory inventoryDaoFactory;

    private InventoryDao inventoryDaoBean;

    @Value("${database.type}")
    private String inventoryDBType;

    @PostConstruct
    public void postConstruct()
    {
        this.inventoryDaoBean = inventoryDaoFactory.getInventoryDao(inventoryDBType);
    }

    public void setInventoryDaoBean(InventoryDao inventoryDaoBean) {
        this.inventoryDaoBean = inventoryDaoBean;
    }

    public InventoryDao getInventoryDaoBean() {
        return inventoryDaoBean;
    }

    /**
     *
     * @apiNote             Get inventory for all products
     * @return              success
     */
    public List<Inventory> getInventory() {
        List<Inventory> inventoryList = getInventoryDaoBean().getInventory();
        inventoryList.sort(new Comparator<Inventory>() {
            @Override
            public int compare(Inventory o1, Inventory o2) {
                return o1.getProductId().compareTo(o2.getProductId());
            }
        });
        return inventoryList;
    }

    /**
     * @param productId     productId for which availability is needed
     * @return              success
     */
    public Inventory getInventory(String productId) {
        return getInventoryDaoBean().getInventory(productId);
    }

    /**
     * @param inventoryReservation list of inventory lines for which reservation is needed
     * @return success
     */
    public boolean reserveInventory(InventoryReservation inventoryReservation) {
        return getInventoryDaoBean().reserveInventory(inventoryReservation);
    }

    /**
     * @param inventory     inventory to be updated
     * @return              success
     */
    public boolean updateInventory(Inventory inventory) {
        return getInventoryDaoBean().insertInventory(inventory);
    }

    /**
     * @param inventory     inventory to be inserted
     * @return              success
     */
    public boolean insertInventory(Inventory inventory) {
        return getInventoryDaoBean().insertInventory(inventory);
    }

    /**
     * Delete all inventory records
     * @return
     */
    public boolean deleteInventory() {
        return getInventoryDaoBean().deleteInventory();
    }
}
