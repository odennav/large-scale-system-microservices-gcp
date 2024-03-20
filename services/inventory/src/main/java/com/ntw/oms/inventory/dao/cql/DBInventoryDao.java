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

import com.ntw.oms.inventory.dao.InventoryDao;
import com.ntw.oms.inventory.entity.Inventory;
import com.ntw.oms.inventory.entity.InventoryReservation;
import com.ntw.oms.inventory.entity.InventoryReservationLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 27/07/20.
 */
@Component("CQL")
public class DBInventoryDao implements InventoryDao {

    private static final Logger logger = LoggerFactory.getLogger(DBInventoryDao.class);

    @Autowired(required = false)
    private CassandraOperations cassandraOperations;

    @Autowired(required = false)
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
    public List<Inventory> getInventory() {
        String selectCql = "select * from inventory";
        List<Inventory> inventories = getCassandraOperations().select(selectCql, Inventory.class);
        if (inventories == null) {
            logger.debug("No inventory found; query={}", selectCql);
            return null;
        }
        logger.debug("Fetched inventory; number of records={}", inventories.size());
        return inventories;
    }

    @Override
    public Inventory getInventory(String productId) {
        String selectCql = "select * from inventory where productId='" + productId + "'";
        DBInventory dbInventory = getCassandraOperations().selectOne(selectCql, DBInventory.class);
        if (dbInventory == null) {
            logger.debug("No inventory found; userId={}", selectCql);
            return null;
        }
        Inventory inventory = dbInventory.getInventory();
        logger.debug("Fetched inventory; context={}", inventory);
        return inventory;
    }

    @Override
    public boolean updateInventory(Inventory inventory) {
        String cql = "update Inventory set quantity=" + inventory.getQuantity() +
                " where productId='" + inventory.getProductId();
        try {
            if (!cqlTemplate.execute(cql)) {
                logger.info("Update inventory failed; context={}", inventory);
                return false;
            }
        } catch (Exception e) {
            logger.error("Update inventory failed due to exception; context={}", inventory);
            logger.error("Exception message:", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean insertInventory(Inventory inventory) {
        DBInventory dbInventory = DBInventory.createInventory(inventory);
        try {
            DBInventory retInventory = getCassandraOperations().insert(dbInventory);
            if (retInventory == null) {
                logger.error("Unable to insert inventory; context={}", inventory);
                return false;
            }
        } catch (Exception e) {
            logger.error("Insert inventory failed due to exception; context={}", inventory);
            logger.error("Exception message:", e);
            return false;
        }
        logger.debug("Inserted inventory; context={}", inventory);
        return true;
    }

    @Override
    public boolean deleteInventory() {
        String cql = "truncate inventory";
        try {
            getCqlTemplate().execute(cql);
        } catch(Exception e) {
            logger.error("Unable to delete inventory records");
            logger.error("Exception message :", e);
            return false;
        }
        logger.debug("All inventory data deleted");
        return true;
    }

    @Override
    public boolean reserveInventory(InventoryReservation inventoryReservation) {
        List<InventoryReservationLine> successfulUpdates = new LinkedList<>();
        boolean transactionSuccessful = true;
        for (InventoryReservationLine line : inventoryReservation.getInventoryReservationLines()) {
            if (! consumeInventory(line.getProductId(), line.getQuantity())) {
                logger.error("Unable to reserve inventory; context={}", line);
                transactionSuccessful = false;
                break;
            }
            logger.debug("Updated inventory reservation line; context={}", line);
            successfulUpdates.add(line);
        }
        if (!transactionSuccessful) {
            // Undo successful updates
            for (InventoryReservationLine line : successfulUpdates) {
                if (! restoreInventory(line.getProductId(), line.getQuantity())) {
                    logger.error("Unable to restore inventory; context={}", line);

                    // ToDo: Put this information in a MQ or a DB table, for a later retry
                }
            }
        }
        return transactionSuccessful;
    }

    private boolean consumeInventory(String productId, float quantity) {
        return retryModifyInventory(productId, quantity, false);
    }

    private boolean restoreInventory(String productId, float quantity) {
        return retryModifyInventory(productId, quantity, true);
    }

    public static final int TXN_RETRY_LIMIT = 3;

    public static final int TXN_ERROR = -1;
    public static final int TXN_RETRY = 0;
    public static final int TXN_SUCCESS = 1;

    private boolean retryModifyInventory(String productId, float quantity, boolean incrementInventory) {
        for (int i=0; i < TXN_RETRY_LIMIT; i++) {
            int result = modifyInventory(productId, quantity, incrementInventory);
            if ( result == TXN_ERROR) {
                return false;
            } else if (result == TXN_SUCCESS) {
                return true;
            }
            // result=TXN_RETRY
        }
        return false;
    }

    private int modifyInventory(String productId, float quantity, boolean incrementInventory) {
        // Get the latest inventory status
        Inventory inventory = getInventory(productId);
        // Compute new inventory quantity
        float newQuantity = (incrementInventory) ? inventory.getQuantity() + quantity :
                inventory.getQuantity() - quantity;
        if (newQuantity < 0) {
            logger.info("Reserve inventory failed for product={} with quantity={} as new quantity is less than zero",
                    productId, quantity);
            return TXN_ERROR;
        }
        StringBuilder cql = new StringBuilder();
        // Compare and Update transaction - with serializable consistency
        cql.append("update Inventory set quantity=").append(newQuantity)
                .append(" where productId='").append(productId)
                .append("' if quantity = ").append(inventory.getQuantity());
        try {
            // returns true if update is applied
            if(! cqlTemplate.execute(cql.toString())) {
                logger.info("Reserve inventory failed for CQL={}", cql.toString());
                return TXN_RETRY;
            }
        } catch(Exception e) {
            logger.error("Exception while executing CQL={}", cql.toString());
            logger.error("Exception message:", e);
            return TXN_ERROR;
        }
        return TXN_SUCCESS;
    }

//    https://docs.datastax.com/en/cql/3.3/cql/cql_reference/cqlInsert.html | cqlUpdate.html
//    https://stackoverflow.com/questions/52388303/how-do-i-find-out-if-the-update-query-was-successful-or-not-in-cassandra-datasta
//
//    cqlsh:oms> update inventory set quantity=9090 where productId='Test-Product-7' if quantity=9997;
//
//    [applied] | quantity
//    -----------+----------
//    False |     9996
//
//    cqlsh:oms> update inventory set quantity=9090 where productId='Test-Product-7' if quantity=9996;
//
//    [applied]
//            -----------
//    True
//
//    cqlsh:oms> update inventory set quantity=9090 where productId='Test-Product-7';
//    cqlsh:oms>

}
