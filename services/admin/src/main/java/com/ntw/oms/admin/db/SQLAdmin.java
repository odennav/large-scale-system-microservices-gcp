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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by anurag on 23/05/19.
 */
@Component
@Qualifier("sqlAdmin")
public class SQLAdmin implements DBAdmin {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment environment;

    private static final Logger logger = LoggerFactory.getLogger(SQLAdmin.class);

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String SQL_GET_DATE_TIME = "Select now()";

    @Override
    public Date getDateTime() {
        Timestamp ts = jdbcTemplate.queryForObject(SQL_GET_DATE_TIME, Timestamp.class);
        logger.debug("Fetched timestamp from DB; TS={}", ts.toString());
        return new Date(ts.getTime());
    }

    @Override
    public String getConnection() {
        return new StringBuilder().append(environment.getProperty("database.postgres.host"))
                .append(":").append(environment.getProperty("database.postgres.port"))
                .append(":").append(environment.getProperty("database.postgres.schema"))
                .toString();
    }

}
