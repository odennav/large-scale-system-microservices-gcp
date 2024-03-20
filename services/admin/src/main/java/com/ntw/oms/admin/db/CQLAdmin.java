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

package com.ntw.oms.admin.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.core.cql.CqlTemplate;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by anurag on 23/05/19.
 */
@Component
@Qualifier("cqlAdmin")
public class CQLAdmin implements DBAdmin {

    private static final Logger logger = LoggerFactory.getLogger(CQLAdmin.class);

    @Autowired(required = false)
    private CqlTemplate cqlTemplate;

    @Autowired
    private Environment environment;

    public CqlTemplate getCqlTemplate() {
        return cqlTemplate;
    }

    public void setCqlTemplate(CqlTemplate cqlTemplate) {
        this.cqlTemplate = cqlTemplate;
    }

    private String CQL_GET_DATE_TIME = "Select toTimestamp(now()) from userauth where id='admin'";

    @Override
    public Date getDateTime() {
        Date date = cqlTemplate.queryForObject(CQL_GET_DATE_TIME, Date.class);
        logger.debug("Fetched timestamp from DB; TS={}", date.toString());
        return date;
    }

    @Override
    public String getConnection() {
        return new StringBuilder().append(environment.getProperty("database.cassandra.hosts"))
                .append(":").append(environment.getProperty("database.cassandra.port"))
                .append(":").append(environment.getProperty("database.cassandra.keySpace"))
                .toString();
    }

}
