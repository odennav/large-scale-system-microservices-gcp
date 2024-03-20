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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Created by anurag on 06/08/20.
 */
@Component
@ConditionalOnBean(DBInventoryDao.class)
public class ReservationTxnManagerFactory {

    @Autowired(required = false)
    TransactionTemplate transactionTemplate;

    @Value("${service.inventory.reservation.txn.type:Pessimistic}")
    private String reservationTxnType;

    public ReservationTxnManager getInstance(JdbcTemplate jdbcTemplate) {
        if (reservationTxnType.equals("Optimistic")) {
            return new OptimisticReservation(jdbcTemplate, transactionTemplate);
        }
        // reservationTxnType.equals("Pessimistic")
        return new PessimisticReservation(jdbcTemplate, transactionTemplate);
    }
}
