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

package com.ntw.oms.inventory.dao.sql;

import com.ntw.oms.inventory.entity.Inventory;
import com.ntw.oms.inventory.entity.InventoryReservation;
import com.ntw.oms.inventory.entity.InventoryReservationLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 06/08/20.
 */
public abstract class ReservationTxnManager {

    public static final int TXN_RETRY_LIMIT = 5;

    public static final int TXN_ERROR = -1;
    public static final int TXN_RETRY = 0;
    public static final int TXN_SUCCESS = 1;

    protected JdbcTemplate jdbcTemplate;
    protected TransactionTemplate transactionTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ReservationTxnManager.class);

    public ReservationTxnManager(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public boolean reserveInventory(InventoryReservation inventoryReservation) {
        int retryCount;
        for (retryCount=0; retryCount < TXN_RETRY_LIMIT; retryCount++) {
            int result = reserveInventoryTransaction(inventoryReservation);
            if (result == TXN_RETRY) {
                try {
                    if (retryCount > 0)
                        Thread.sleep(10*retryCount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("Retry again : retryCount = {}; reservation={}", retryCount, inventoryReservation);
                continue;
            } else if (result == TXN_ERROR) {
                return false;
            } else {
                // result == InventoryDao.TXN_SUCCESS
                return true;
            }
        }
        logger.error("Unable to update inventory due to continued race condition for {}", inventoryReservation);
        return false;
    }

    protected int reserveInventoryTransaction(InventoryReservation inventoryReservation) {
        return (Integer) transactionTemplate.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                try {
                    return reserveInventoryExecute(inventoryReservation);
                } catch(ReservationTxnRetryException re) {
                    status.setRollbackOnly();
                    return TXN_RETRY;
                } catch(Exception e) {
                    status.setRollbackOnly();
                    return TXN_ERROR;
                }
            }
        });
    }

    protected abstract int reserveInventoryExecute(InventoryReservation inventoryReservation)
            throws Exception;

    protected abstract String getInventoryQuery();

    protected List<Inventory> getInventory(InventoryReservation inventoryReservation) {
        List<Inventory> inventoryList = new LinkedList<>();
        for (InventoryReservationLine line : inventoryReservation.getInventoryReservationLines()) {
            String inventorySql = getInventoryQuery();
            Inventory inventory = jdbcTemplate.queryForObject(inventorySql, new Object[]{line.getProductId()},
                    new BeanPropertyRowMapper<>(Inventory.class));
            inventoryList.add(inventory);
        }
        return inventoryList;
    }
}
