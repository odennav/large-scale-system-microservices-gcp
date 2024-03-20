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

import com.ntw.common.config.AppConfig;
import com.ntw.common.entity.Role;
import com.ntw.common.security.Secured;
import com.ntw.oms.inventory.entity.Inventory;
import com.ntw.oms.inventory.entity.InventoryReservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by anurag on 24/03/17.
 */

/**
 * Rest interface for adding, searching, modifying and deleting products
 */
@RestController
@RequestMapping(AppConfig.INVENTORY_RESOURCE_PATH)
public class InventoryService {

    @Autowired
    InventoryServiceImpl inventoryServiceBean;

    public InventoryServiceImpl getInventoryServiceBean() {
        return inventoryServiceBean;
    }

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    /**
     * Get inventory for all products. Only for user role admin.
     * @return      list of product inventory
     */
    @Secured({Role.ADMIN})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Inventory>> getAllInventory() {
        List<Inventory> inventoryList = getInventoryServiceBean().getInventory();
        if (inventoryList == null) {
            logger.info("Could not get all inventoryList");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        logger.info("Get inventoryList; size={}", inventoryList.size());
        return ResponseEntity.ok().body(inventoryList);
    }

    /**
     * Get inventory for a product
     * @param productId     id of product for which inventory is to be fetched
     * @return
     */
    @Secured({Role.ADMIN, Role.USER})
    @GetMapping(path = AppConfig.INVENTORY_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Inventory> getInventory(@PathVariable("productId") String productId) {
        Inventory inventory = getInventoryServiceBean().getInventory(productId);
        if (inventory == null) {
            logger.info("Could not get inventory for product id {}", productId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(inventory);
        }
        logger.info("Get inventory; context={}", inventory);
        return ResponseEntity.ok().body(inventory);
    }

    /**
     * Insert inventory for a product. To be used by admin role only.
     * @param productId     product id
     * @param inventory     inventory object
     * @return
     */
    @Secured({Role.ADMIN})
    @PutMapping(path = AppConfig.INVENTORY_PATH, consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity insertInventory(@PathVariable("productId") String productId,
                                          @RequestBody Inventory inventory) {
        if (getInventoryServiceBean().insertInventory(inventory)) {
            logger.info("Post Inventory successful; context={}", inventory);
            return ResponseEntity.ok("INSERTED INVENTORY");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INSERT FAILED");
    }

    /**
     * Update inventory
     * @param inventory     updated inventory object
     * @return
     */
    @Secured({Role.ADMIN, Role.USER})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity updateInventory(@RequestBody Inventory inventory) {
        if(getInventoryServiceBean().updateInventory(inventory)) {
            logger.info("Update Inventory successful; context={}", inventory);
            return ResponseEntity.ok().body("UPDATE SUCCESSFUL");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("UPDATE FAILED");
    }

    /**
     * Reserve inventory for product order lines
     * @param inventoryReservation  object carrying inventory requirements of order lines
     * @return
     */
    @Secured({Role.ADMIN,Role.USER})
    @PostMapping(path= AppConfig.INVENTORY_RESERVATION_PATH, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity reserveInventory(@RequestBody InventoryReservation inventoryReservation) {
        if(getInventoryServiceBean().reserveInventory(inventoryReservation)) {
            logger.info("Reserve Inventory successful; context={}", inventoryReservation);
            return ResponseEntity.ok().body("SUCCESS");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("UPDATE FAILED");
    }

    @Secured({Role.ADMIN})
    @DeleteMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity deleteInventory() {
        if (getInventoryServiceBean().deleteInventory()) {
            logger.info("Delete Inventory successful");
            return ResponseEntity.ok("DELETED ALL INVENTORY");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DELETE INVENTORY FAILED");
    }

}
