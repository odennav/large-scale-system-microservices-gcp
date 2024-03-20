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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * Created by anurag on 06/08/20.
 */
public class PessimisticReservation extends ReservationTxnManager {

    private static final Logger logger = LoggerFactory.getLogger(PessimisticReservation.class);

    private static final String invSelectQuery = "select productId, quantity from inventory where productId=? for update";
    public static final String invUpdateSql = "update Inventory set quantity=? where productId=?";

    public PessimisticReservation(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        super(jdbcTemplate, transactionTemplate);
    }

    @Override
    protected String getInventoryQuery() {
        return invSelectQuery;
    }

    @Override
    protected int reserveInventoryExecute(InventoryReservation inventoryReservation)
            throws Exception {
        List<Inventory> inventoryList = getInventory(inventoryReservation);

        for (int i=0; i<inventoryReservation.getInventoryReservationLines().size(); i++) {
            InventoryReservationLine line = inventoryReservation.getInventoryReservationLines().get(i);
            Inventory inventory = inventoryList.get(i);
            int retValue;
            try {
                retValue = jdbcTemplate.update(invUpdateSql,
                        new Object[]{inventory.getQuantity() - line.getQuantity(), line.getProductId()});
            } catch(Exception e) {
                logger.error("Exception while updating inventory reservation line; context={}", line);
                logger.error("Exception message: ", e);
                throw e;
            }
            if (retValue <= 0) {
                logger.error("Unable to update inventory; context={}", line);
                throw new Exception("Unable to update inventory reservation line : "+line);
            }
            logger.debug("Updated inventory reservation line; context={}", line);
        }
        return TXN_SUCCESS;
    }
}
